package element.io.mq.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Slf4j
@Component
public class CustomConfirmCallBack implements RabbitTemplate.ConfirmCallback {

	/**
	 * 无论服务端Broker成功接收到了消息还是失败,只要消息到达了Broker,confirmCallback都会执行,通过ack来判断是否成功
	 * Confirmation callback.
	 *
	 * @param correlationData correlation data for the callback. 消息的关联标识,核心就是消息的id
	 * @param ack             true for ack, false for nack 是否接收成功,true成功
	 * @param cause           An optional cause, for nack, when available, otherwise null. 出错的原因
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (ack) {
			//	 Broker成功接收到发送的消息
			ok(correlationData);
		} else {
			//	 失败
			processFailedMessage(correlationData, cause);
		}

	}

	private void processFailedMessage(CorrelationData correlationData, String cause) {
		if (Objects.isNull(correlationData)) {
			log.info("给了一个空数据");
			return;
		}
		String id = correlationData.getId();
		log.error("序号为:{}的消息发送失败,错误原因{}", id, cause);
		//	 可以进行重发...
	}


	private void ok(CorrelationData correlationData) {
		if (Objects.isNull(correlationData)) {
			log.info("给了一个空数据");
			return;
		}
		String id = correlationData.getId();
		log.error("序号为: {}的消息发送成功", id);
	}


}
