package element.io.mq.service.impl;

import com.rabbitmq.client.Channel;
import element.io.mq.domain.UserInfo;
import element.io.mq.domain.Weather;
import element.io.mq.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
//@RabbitListener(queues = {"guli"}, queuesToDeclare = {@Queue(name = "guli", durable = "true", exclusive = "false")}, bindings = {@QueueBinding(value = @Queue("guli"), exchange = @Exchange("dd"), key = "weather")})
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

	@Resource
	private AmqpTemplate amqpTemplate;

	@Resource
	private RabbitTemplate rabbitTemplate;


	// Object直接接收消息,其实接收的就是Message对象
	@Override
	public void receiveMessage(Message msg) {
		byte[] bytes = msg.getBody();
		MessageProperties properties = msg.getMessageProperties();
		log.info("接收到的消息{},类型{}", msg, msg.getClass().getName());
	}


	@Override
// String 直接接收消息体中的内容,需要手动解析
	public void receiveMessage(String msg) {
		log.info("接收到的消息{}", msg);
	}


	// 可以直接接收对象数据,使用对象的原始数据类型
	//@RabbitHandler
	@Override
	public void receiveMessage(UserInfo userInfo) {
		log.info("接收到的对象信息{}", userInfo);
	}

	//@RabbitHandler
	@Override
	public void receiveWeatherInfo(Message message, Weather weather, Channel channel) throws InterruptedException {
		long seq = message.getMessageProperties().getDeliveryTag();
		String id = message.getMessageProperties().getMessageId();
		log.info("接收到了消息,消息的序号{}", seq);
		if (System.currentTimeMillis() % 4 == 0) {
			try {
				// 拒绝消息,第一个参数代表消息的序号,第二个参数代表是否是批量拒绝,
				// 如果第二个参数的值为true,那么就代表批量拒绝序号小于等于当前序号的所有消息
				// 第三个参数代表被拒绝的消息是否能够重新进入到队列中去,如果为true那么被拒绝的消息就会被重新放入到队列中去
				//channel.basicReject(); basicReject和basicNack差不多,区别在basicNack不能够批量拒绝
				channel.basicNack(seq, false, false);
				log.info("拒绝确认id为{}的消息", id);
			} catch (IOException e) {
				log.error("拒绝失败");
			}
		} else {
			try {
				// 确认消息,basicAck的第一个参数是当前要确认的消息在当前信道channel中的序号
				// 第二个参数表示是否是批量确认,如果是批量确认,则代表之前所有序号小于等于当前序号的消息都会被确认,
				// 就类似于TCP的滑动窗口的确认机制
				channel.basicAck(seq, false);
				log.info("确认了id为{}的消息", id);
			} catch (IOException e) {
				log.error("id为{},序号为{}的消息确认失败", id, seq);
			}
		}

	}

	/**
	 * @RabbitListener 既可以用在类上, 也可以用在方法上.
	 * 用在类上就可以为当前类中的所有方法指定所有要监听的队列,
	 * 但是接收消息的方法上必须要标注@RabbitHandler注解
	 * Spring Boot可以根据消息转换后的类型选择调用对应的回调函数
	 * 这样就可以在一个类中同时监听多个队列或者根据同一个队列中接收到的不同类型的消息做出不同的处理
	 * 更加灵活
	 */

	@Override
	public void pushWeatherInfo(Weather weather) {
		String routingKey = "weather";
		String exchange = "dd";
		rabbitTemplate.convertAndSend(exchange, routingKey, weather, new CorrelationData(UUID.randomUUID().toString()));
	}

	@RabbitListener(queues = {"order.release.queue"})
	public void consumeDelayedMessage(Message message, Channel channel, Weather msg) {
		long seq = message.getMessageProperties().getDeliveryTag();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String current = dateFormat.format(new Date());
		String str = dateFormat.format(msg.getTime());
		log.info("source:{}", message);
		log.info("接收到消息,当前时间是:{},消息的发送时间:{}", current, str);
		try {
			channel.basicAck(seq, false);
		} catch (IOException e) {
			throw new RuntimeException("消息确认失败");
		}
	}

}
