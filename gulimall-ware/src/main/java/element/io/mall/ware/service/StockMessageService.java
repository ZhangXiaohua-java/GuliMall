package element.io.mall.ware.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import element.io.mall.common.msg.OrderTo;
import element.io.mall.common.msg.ReleaseStockTaskTo;
import element.io.mall.common.msg.StockLockedTo;
import element.io.mall.common.service.OrderFeignRemoteClient;
import element.io.mall.common.to.OrderStatusTo;
import element.io.mall.ware.entity.WareOrderTaskDetailEntity;
import element.io.mall.ware.entity.WareOrderTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.MQConstants.STOCK_RELEASE_QUEUE;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Service
@Slf4j
@RabbitListener(queues = {STOCK_RELEASE_QUEUE})
public class StockMessageService {

	@Resource
	private WareOrderTaskService wareOrderTaskService;

	@Resource
	private OrderFeignRemoteClient orderFeignRemoteClient;

	@Resource
	private WareOrderTaskDetailService wareOrderTaskDetailService;

	@Resource
	private WareSkuService wareSkuService;

	@RabbitHandler
	public void consumeReleaseMessage(StockLockedTo to, Message message, Channel channel) throws IOException {
		long seq = message.getMessageProperties().getDeliveryTag();
		WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(to.getTaskId());
		if (Objects.isNull(orderTaskEntity)) {
			//	如果查到的任务明细也是空的,则代表库存服务自己回滚了,就不需要解锁库存了,直接确认消息即可
			channel.basicAck(seq, false);
			log.info("库存没有被锁定,直接确认消息");
		} else {
			String orderSn = orderTaskEntity.getOrderSn();
			OrderStatusTo statusTo = orderFeignRemoteClient.orderStatus(orderSn);
			// 订单被取消或者不存在,如果服务返回的结果的status值,即使订单不存在也是返回一个状态值,和取消的状态值一样
			if (statusTo.getStatus() == 4) {
				//	 解锁库存
				try {
					boolean result = wareSkuService.unlockStock(to);
					if (result) {
						channel.basicAck(seq, false);
						log.info("解锁库存成功,消息确认");
					}
				} catch (Exception e) {
					channel.basicReject(seq, true);
					log.info("消息重新入队");
					e.printStackTrace();
					log.error("出错了", e);
				}
			}
			log.info("消费到消息{}", to);
		}
	}


	@RabbitHandler
	public void releaseLockedStock(OrderTo order, Message message, Channel channel) throws IOException {
		long seq = message.getMessageProperties().getDeliveryTag();
		//		 查询任务单信息
		LambdaQueryWrapper<WareOrderTaskEntity> taskQuery = new LambdaQueryWrapper<WareOrderTaskEntity>()
				.eq(WareOrderTaskEntity::getOrderSn, order.getOrderSn());
		WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getOne(taskQuery);
		// 保证幂等性
		LambdaQueryWrapper<WareOrderTaskDetailEntity> detailTasksQuery = new LambdaQueryWrapper<>();
		detailTasksQuery.eq(WareOrderTaskDetailEntity::getTaskId, wareOrderTask.getId())
				.eq(WareOrderTaskDetailEntity::getLockStatus, 1);
		List<WareOrderTaskDetailEntity> taskDetails = wareOrderTaskDetailService.list(detailTasksQuery);
		if (CollectionUtils.isEmpty(taskDetails)) {
			channel.basicAck(seq, false);
		} else {
			// 主动解锁库存逻辑
			StockLockedTo lockedTo = new StockLockedTo();
			lockedTo.setTaskId(wareOrderTask.getId());
			List<ReleaseStockTaskTo> tos = taskDetails.stream().map(e -> {
				ReleaseStockTaskTo to = new ReleaseStockTaskTo();
				to.setSkuId(e.getSkuId());
				to.setSkuNum(e.getSkuNum());
				to.setWareId(e.getWareId());
				return to;
			}).collect(Collectors.toList());
			lockedTo.setReleaseTos(tos);
			//	 完成解锁库存的逻辑
			boolean result = wareSkuService.unlockStock(lockedTo);
			if (result) {
				log.info("主动解锁库存成功");
				channel.basicAck(seq, false);
			} else {
				channel.basicReject(seq, true);
			}
		}

	}


}
