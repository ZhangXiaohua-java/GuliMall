package element.io.mall.order.service;

import com.rabbitmq.client.Channel;
import element.io.mall.common.enumerations.MQConstants;
import element.io.mall.common.msg.SecKillOrderTo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 张晓华
 * @date 2022-12-12
 */
@RabbitListener(queues = {MQConstants.SEC_KILL_QUEUE})
@Component
public class SecKillOrderMsgService {

	@Resource
	private OrderService orderService;

	@Resource
	private OrderItemService orderItemService;

	@RabbitHandler
	public void createSecKillOrder(SecKillOrderTo secKillOrderTo, Message message, Channel channel) throws IOException {
		long seq = message.getMessageProperties().getDeliveryTag();
		try {
			orderService.saveSecKillOrder(secKillOrderTo);
			channel.basicAck(seq, false);
		} catch (Exception e) {
			channel.basicReject(seq, true);
		}

	}


}
