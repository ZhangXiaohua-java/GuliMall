package element.io.mall.order.config;

import element.io.mall.common.domain.MqMessageEntity;
import element.io.mall.common.enumerations.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Slf4j
public class CustomConfirmCallback implements RabbitTemplate.ConfirmCallback {

	private RabbitTemplate rabbitTemplate;

	private RedisTemplate<String, Object> redisTemplate;


	public CustomConfirmCallback(RabbitTemplate rabbitTemplate, RedisTemplate<String, Object> redisTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean b, String s) {
		String id = MQConstants.REDIS_KEY_PREFIX + correlationData.getId();
		Object o = redisTemplate.opsForValue().get(id);
		if (Objects.isNull(o)) {
			return;
		}
		MqMessageEntity message = (MqMessageEntity) o;
		if (b) {
			message.setMessageStatus(1);
			redisTemplate.opsForValue().set(MQConstants.REDIS_KEY_PREFIX + correlationData.getId(), message);
			log.info("id为{}的消息已经投递到交换机", correlationData.getId());
		} else {
			log.error("重发id{}为xxx的消息", correlationData.getId());
			rabbitTemplate.convertAndSend(message.getToExchane(), message.getRoutingKey(), message.getContent());
		}
	}

}
