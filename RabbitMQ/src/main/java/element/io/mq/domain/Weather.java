package element.io.mq.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Data
public class Weather {

	private Date time;

	private String rate;

	private String desc;

	private String templature;

	public Weather() {
	}

	public Weather(Date time, String rate, String desc) {
		this.time = time;
		this.rate = rate;
		this.desc = desc;
	}


}
