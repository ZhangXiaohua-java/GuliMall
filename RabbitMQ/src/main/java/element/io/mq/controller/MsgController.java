package element.io.mq.controller;

import element.io.mq.domain.Weather;
import element.io.mq.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@RestController
public class MsgController {

	@Resource
	private MessageService messageService;


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


}
