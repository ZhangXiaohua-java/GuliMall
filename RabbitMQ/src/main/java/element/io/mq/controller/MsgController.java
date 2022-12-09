package element.io.mq.controller;

import element.io.mq.domain.Weather;
import element.io.mq.service.MessageService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@RestController
public class MsgController {

	@Resource
	private MessageService messageService;

	@Resource
	private AmqpTemplate amqpTemplate;


	@GetMapping(value = "/msg")
	public String msg() {
		for (int i = 0; i < 10; i++) {
			Weather weather = new Weather();
			weather.setDesc("今天不太适合外出运动");
			weather.setTime(new Date());
			weather.setTemplature("10" + i + "°");
			weather.setRate("bad");
			messageService.pushWeatherInfo(weather);
		}
		return "ok";
	}

	@GetMapping("/send")
	public String send() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Weather weather = new Weather(new Date(), "一般般", "有点冷");
		amqpTemplate.convertAndSend("order.event.exchange", "order.delay", weather);
		return "ok";
	}


}
