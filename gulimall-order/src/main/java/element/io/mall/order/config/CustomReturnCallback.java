package element.io.mall.order.config;

import element.io.mall.common.domain.MqMessageEntity;
import element.io.mall.common.enumerations.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Slf4j
public class CustomReturnCallback implements RabbitTemplate.ReturnCallback {

	private RabbitTemplate rabbitTemplate;

	private RedisTemplate<String, Object> redisTemplate;

	public CustomReturnCallback(RabbitTemplate rabbitTemplate, RedisTemplate<String, Object> redisTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		String id = message.getMessageProperties().getCorrelationId();
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		MqMessageEntity msg = (MqMessageEntity) ops.get(MQConstants.REDIS_KEY_PREFIX + id);
		if (Objects.isNull(msg)) {
			return;
		}
		msg.setMessageStatus(2);
		// 将发送失败的消息存入redis,启动定时任务重发
		log.error("存入redis,启动定时任务重发消息");
		ops.set(MQConstants.REDIS_KEY_PREFIX + id, msg);
	}

}
