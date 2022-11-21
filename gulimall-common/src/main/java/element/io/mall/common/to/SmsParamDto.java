package element.io.mall.common.to;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@Data
public class SmsParamDto {

	/**
	 *
	 */

	private String mobile;

	private String param;

	private String smsSignId;

	private String templateId;


}
