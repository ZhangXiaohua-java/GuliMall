package element.io.mall.common.enumerations;

import lombok.Getter;

/**
 * @author 张晓华
 * @date 2022-11-7
 */
@Getter
public enum PurchaseStatusEnum {


	CREATED(0, "创建"),
	ASSIGNED(1, "已分配"),
	RECEIVED(2, "已被领取"),
	FINISHED(3, "已完成"),
	FAILED(4, "失败");


	private Integer code;

	private String desc;

	PurchaseStatusEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	

}
