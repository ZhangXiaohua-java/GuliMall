package element.io.mall.common.enumerations;

import lombok.Getter;

/**
 * @author 张晓华
 * @date 2022-11-1
 * 错误枚举:
 * 五位长度的状态码,前两位代表模块信息
 * 后三位代表错误类型
 * 10 代表通用的错误代码
 * 20 代表会员模块
 * 30 代表产品模块
 * 40 代表优惠券模块
 * 50 代表订单模块
 * 60 代表库存模块
 * 70 代表第三方服务模块
 */
@Getter
public enum ExceptionStatusEnum {

	SYSTEM_UNKNOWN_EXCEPTION(10001L, "未知异常,请联系系统管理员"),
	PARAM_NOT_VALID_EXCEPTION(100010, "参数不合法"),

	CODE_REQUEST_FREQUENTLY_EXCEPTION(100020, "验证码请求频繁");

	private long code;

	private String msg;

	ExceptionStatusEnum(long code, String msg) {
		this.code = code;
		this.msg = msg;
	}


}
