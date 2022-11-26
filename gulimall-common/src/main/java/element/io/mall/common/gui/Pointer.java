package element.io.mall.common.gui;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-11-25
 */
@Slf4j
@Data
public class Pointer {

	public static void main(String[] args) throws AWTException, InterruptedException {
		TimeUnit.SECONDS.sleep(2);
		for (int i = 0; i < 10; i++) {
			cancel();
			TimeUnit.MILLISECONDS.sleep(300);
		}
	}
//	 963, 200
//	 1501, 348

	public static void cancel() throws AWTException {
		Robot robot = new Robot();
		robot.mouseMove(504, 585);
		reportPointer();
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mouseMove(1496, 345);
		reportPointer();
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public static void reportPointer() {
		Point point = MouseInfo.getPointerInfo().getLocation();
		double x = point.getX();
		double y = point.getY();
		log.info("当前鼠标的位置X轴:{}Y轴: {}", x, y);
	}

	/**
	 https://www.douyin.com/aweme/v1/web/commit/item/digg/
	 ?device_platform=webapp&aid=6383&channel=channel_pc_web&pc_client_type=1&version_code=170400&version_name=17.4.0
	 &cookie_enabled=true&screen_width=1536&screen_height=864&browser_language=zh-CN&browser_platform=Win32
	 &browser_name=Chrome&browser_version=107.0.0.0
	 &browser_online=true&engine_name=Blink&engine_version=107.0.0.0&os_name=Windows&os_version=10
	 &cpu_core_num=8&device_memory=8&platform=PC
	 &downlink=10&effective_type=4g&round_trip_time=50
	 &webid=7137971236948428301
	 &msToken=kZp0TgsiZkjo9CALeF2DHqj9ri4EH68139G_otol6ucN3xgKezLBr8sPvgeqSV9_b_DoFJZ2UYxkyR-uJQzEZjL8y6-fT9Orhc8zAtjt3OgOGMRaOvCx5OQ=
	 &X-Bogus=DFSzswVLb6GCQzdJSpfRde9WX7rj

	 */

}
