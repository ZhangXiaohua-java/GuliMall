package element.io.mall.common.to;

import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@SuppressWarnings({"all"})
@Data
public class SmsDto {

	/**
	 * {
	 * "msg": "成功",
	 * "smsid": "16565614329364584123421",  //批次号。可通过该ID查询发送状态或者回复短信。API接口可联系客服获取。
	 * "code": "0",
	 * "balance": "1234"  //账户剩余次数
	 * }
	 */


	private String msg;

	private String smsid;

	private Integer code;

	private Long balance;

	private List<String> ILLEGAL_WORDS;

}
