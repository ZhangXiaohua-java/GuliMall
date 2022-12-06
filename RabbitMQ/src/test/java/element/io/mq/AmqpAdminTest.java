package element.io.mq;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
public class AmqpAdminTest {

	@Resource
	private AmqpAdmin amqpAdmin;

	@Test
	public void autoWire() {
		log.info("注入的对象{}", amqpAdmin);
	}

	@Test
	public void createExchange() {
		// 不声明autoDelete和internal即可
		// 创建的是一个持久化的非自动删除的,非内部的交换机
		Exchange guliExchange = ExchangeBuilder.directExchange("guli").durable(true).build();
		amqpAdmin.declareExchange(guliExchange);
	}

	@Test
	public void createQueue() {
		// 创建一个持久化的,非排外的,非自动删除的队列
		Queue guliQueue = QueueBuilder.durable("guli").build();
		// 返回创建成功的队列名字,在创建随机队列时才有用
		String queue = amqpAdmin.declareQueue(guliQueue);
	}

	@Test
	public void createRandomQueue() throws InterruptedException {
		// 排外的,自动删除的非持久化的随机队列
		Queue queue = amqpAdmin.declareQueue();
		log.info("创建的随机队列{}", queue);
		Thread.sleep(10000);
	}

	@Test
	public void bind() {
		Queue guliQueue = new Queue("guli");
		DirectExchange guliExchange = new DirectExchange("guli", true, false);
		Binding binding = BindingBuilder.bind(guliQueue).to(guliExchange).with("guli");
		amqpAdmin.declareBinding(binding);
	}

	@Test
	public void unbind() {
		Binding binding = new Binding("guli", Binding.DestinationType.QUEUE, "guli", "guli", null);
		amqpAdmin.removeBinding(binding);
	}

	@Test
	public void delete() {
		// 仅在队列没有被使用并且队列中没有消息时才能删除成功,否则删除失败,失败会抛出IOException
		amqpAdmin.deleteQueue("guli", true, true);
	}


	@Test
	public void any() {
		DirectExchange exchange = ExchangeBuilder.directExchange("dd").durable(true).build();
		amqpAdmin.declareExchange(exchange);
		Queue report = QueueBuilder.durable("report").build();
		amqpAdmin.declareQueue(report);
		Binding binding = BindingBuilder.bind(report).to(exchange).with("weather");
		amqpAdmin.declareBinding(binding);
	}


}
