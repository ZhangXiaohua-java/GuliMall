package element.io.mall.ware.config;

import element.io.mall.common.enumerations.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-12-6
 */
@Configuration
public class RabbitMqConfig {

	/**
	 * stock.delay.queue routingKey stock.delay
	 * stock.release.queue routingKey stock.release
	 * stock.event.exchange
	 */


	@Bean
	public TopicExchange stockEventExchange() {
		return ExchangeBuilder.topicExchange("stock.event.exchange")
				.durable(true).build();
	}

	@Bean
	public Queue stockDelayQueue() {
		return QueueBuilder.durable("stock.delay.queue")
				.deadLetterExchange("stock.event.exchange")
				.deadLetterRoutingKey("stock.release")
				.ttl(Long.valueOf(TimeUnit.MINUTES.toMillis(2L)).intValue()).build();
	}

	@Bean
	public Queue stockReleaseQueue() {
		return QueueBuilder.durable("stock.release.queue").build();
	}


	@Bean
	public Binding stockDelayBinding(Queue stockDelayQueue, TopicExchange stockEventExchange) {
		return BindingBuilder.bind(stockDelayQueue).to(stockEventExchange)
				.with("stock.delay");
	}


	@Bean
	public Binding stockReleaseBinding(Queue stockReleaseQueue, TopicExchange stockEventExchange) {
		return BindingBuilder.bind(stockReleaseQueue).to(stockEventExchange)
				.with("stock.release");
	}


	@Bean
	public Binding stockReleaseBindingWithOrder(Queue stockReleaseQueue) {
		DirectExchange exchange = ExchangeBuilder.directExchange(MQConstants.ORDER_EVENT_EXCHANGE).durable(true).build();
		return BindingBuilder.bind(stockReleaseQueue).to(exchange)
				.with(MQConstants.ORDER_EVENT_STOCK_RELEASE_BINDING);
	}


	@Bean
	public Queue subStockQueue() {
		return new Queue(MQConstants.SUB_STOCK_QUEUE, true, false, false);
	}

	@Bean
	public Binding subStockQueueBinding(Queue subStockQueue, TopicExchange stockEventExchange) {
		return BindingBuilder.bind(subStockQueue).to(stockEventExchange)
				.with(MQConstants.SUB_STOCK_QUEUE_BINDING);
	}
	

	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}


}
