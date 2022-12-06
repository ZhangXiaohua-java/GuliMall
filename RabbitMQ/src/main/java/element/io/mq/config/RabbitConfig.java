package element.io.mq.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Configuration
public class RabbitConfig implements InitializingBean {

	@Resource
	RabbitTemplate rabbitTemplate;


	@Resource
	RabbitTemplate.ConfirmCallback callback;

	@Resource
	private RabbitTemplate.ReturnsCallback returnsCallback;

	/**
	 * RabbitMQ消息的可靠性保证的解决方案
	 * 消息的可靠送达 一
	 * 1: 保证生产者的消息能够被准确地投递到队列中
	 * 1.1: confirmCallback,确认模式,
	 * Broker接收到消息之后给生产者一个确认消息,这个可以保证消息被投递到交换机中,成功和失败都会有执行回调函数
	 * <p>
	 * 1.2: returnCallback 确认Broker可以正确地将消息从交换机路由到队列中去,只有在消息投递到队列失败之后才会执行回调函数.
	 * <p>
	 * 1.3 发送端其实还可以使用事务模式,但是这种方式性能损耗特别大,不选择使用
	 * 消费者端
	 * 消费者端保证消费者能够及时的消费消息,避免消息丢失和消费失败导致的消息丢失
	 * 2.1: ack机制,手动确认消息,消费失败就不需要确认消息
	 * <p>
	 * 需要手动修改配置文件来开启消息的发送确认模式
	 * publisher-confirm-type: correlated 对应1.1的将消息发送给Broker的确认配置 confirmCallback
	 * publisher-returns: true 对应1.2的交换机将消息投递给队列出错的确认配置项 returnsCallback
	 * template:
	 * mandatory: true 这一项是专门为returnsCallback设置的,这个配置项指定以异步的方式执行returnsCallback
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("--->>>" + callback.getClass().getName());
		rabbitTemplate.setConfirmCallback(callback);
		rabbitTemplate.setReturnsCallback(returnsCallback);
	}


}
