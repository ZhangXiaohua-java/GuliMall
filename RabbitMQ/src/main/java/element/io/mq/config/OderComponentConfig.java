package element.io.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张晓华
 * @date 2022-12-6
 */
@Configuration
public class OderComponentConfig {
	/**
	 * order.delay.queue binding order.delay
	 * order.release.queue binding  order.release
	 * order.event.exchange 共用一个交换机
	 */

	@Bean
	public Queue delayQueue() {
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", "order.event.exchange");
		arguments.put("x-dead-letter-routing-key", "order");
		arguments.put("x-message-ttl", 60000);
		return new Queue("order.delay.queue", true, false, false, arguments);
	}

	@Bean
	public Queue releaseQueue() {
		return QueueBuilder.durable("order.release.queue").build();
	}

	@Bean
	public DirectExchange orderEventExchange() {
		return ExchangeBuilder.directExchange("order.event.exchange")
				.durable(true).build();
	}

	@Bean
	public Binding delayQueueBinding(Queue delayQueue, DirectExchange exchange) {
		return BindingBuilder.bind(delayQueue).to(exchange).with("order.delay");
	}

	@Bean
	public Binding releaseQueueBinding(Queue releaseQueue, DirectExchange orderEventExchange) {
		return BindingBuilder.bind(releaseQueue).to(orderEventExchange).with("order");
	}


}
