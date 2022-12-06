package element.io.mq.service;

import com.rabbitmq.client.Channel;
import element.io.mq.domain.UserInfo;
import element.io.mq.domain.Weather;
import org.springframework.amqp.core.Message;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
public interface MessageService {

	void receiveMessage(Message msg);

	default void receiveMessage(String msg) {

	}

	default void receiveMessage(UserInfo userInfo) {

	}


	void receiveWeatherInfo(Message message, Weather weather, Channel channel) throws InterruptedException;

	void pushWeatherInfo(Weather weather);

}
