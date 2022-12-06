package element.io.mq.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Slf4j
@Component
public class CustomReturnCallback implements RabbitTemplate.ReturnsCallback {

	@Lazy
	@Resource
	private RabbitTemplate rabbitTemplate;

	/**
	 * returnCallback只会在交换机将消息路由到队列失败之后才会执行,成功则不会执行
	 * 可以在这个方法中完成消息的重发.
	 * private final Message message;  发送失败的消息
	 * <p>
	 * private final int replyCode;    路由失败的状态码
	 * <p>
	 * private final String replyText; 路由错误的原因
	 * <p>
	 * private final String exchange; 消息路由失败的交换机的名字
	 * <p>
	 * private final String routingKey; 路由出错的路由键
	 */
	@Override
	public void returnedMessage(ReturnedMessage returned) {
		Message message = returned.getMessage();
		String msg = new String(message.getBody(), StandardCharsets.UTF_8);
		String exchange = returned.getExchange();
		String routingKey = returned.getRoutingKey();
		int replyCode = returned.getReplyCode();
		String cause = returned.getReplyText();
		log.error("消息投递失败,状态码:{},错误原因{},错误的交换机{},路由键{}", replyCode, cause, exchange, routingKey);
		log.error("投递失败的消息的具体内容{}", msg);
		rabbitTemplate.send(exchange, "weather", message, new CorrelationData(UUID.randomUUID().toString()));
	}


}
