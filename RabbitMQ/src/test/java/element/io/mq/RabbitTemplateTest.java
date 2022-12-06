package element.io.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import element.io.mq.domain.UserInfo;
import element.io.mq.domain.Weather;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Slf4j
@SpringBootTest
public class RabbitTemplateTest {

	@Resource
	private AmqpTemplate amqpTemplate;

	@Resource
	private RabbitTemplate rabbitTemplate;


	@Test
	public void sendSimpleMessage() {
		// 将消息投递到交换机中并指定消息的路由键,消息到达Broker后由交换机将消息投递到队列中去
		amqpTemplate.convertAndSend("guli", "guli", "hello 谷粒");
	}

	@Test
	public void sendMessage() {
		byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
		Message message = new Message(bytes);
		// 给当前的模板绑定要使用的routingKey和交换机,这是RabbitMqTemplate扩展出的方法
		rabbitTemplate.setRoutingKey("guli");
		rabbitTemplate.setExchange("guli");
		rabbitTemplate.send(message);
	}

	@Test
	public void sendObject() {
		UserInfo userInfo = new UserInfo("张三", 100, "男");
		// 直接发送对象数据要求该类必须实现JDK的序列化接口,想要使用JSON序列化只需要在容器中配置一个MessageConvert的实例即可
		amqpTemplate.convertAndSend("guli", "guli", userInfo);
	}

	@Test
	public void send() throws JsonProcessingException {
		for (int i = 0; i < 10; i++) {
			Weather weather = new Weather();
			weather.setTime(new Date());
			weather.setRate("bad");
			weather.setDesc("今天不太适合锻炼 " + i);
			rabbitTemplate.convertAndSend("dd", "weather", weather);
		}
	}


}
