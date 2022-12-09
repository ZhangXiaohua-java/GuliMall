package element.io.mall.ware.service.impl;

import com.rabbitmq.client.Channel;
import element.io.mall.common.enumerations.MQConstants;
import element.io.mall.common.msg.OrderTo;
import element.io.mall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 张晓华
 * @date 2022-12-9
 */
@Slf4j
@Service
@RabbitListener(queues = {MQConstants.SUB_STOCK_QUEUE})
public class StockMessageServiceTwo {

	@Resource
	private WareSkuService wareSkuService;


	@RabbitHandler
	public void consumingSubStockMessage(OrderTo order, Message message, Channel channel) throws IOException {
		log.info("接收到扣减库存的消息{}", order);
		long seq = message.getMessageProperties().getDeliveryTag();
		if (wareSkuService.subStock(order.getOrderSn())) {
			channel.basicAck(seq, false);
		} else {
			channel.basicReject(seq, true);
		}
	}

}
