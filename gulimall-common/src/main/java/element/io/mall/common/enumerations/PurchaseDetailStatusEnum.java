package element.io.mall.common.enumerations;

import lombok.Getter;

/**
 * @author 张晓华
 * @date 2022-11-7
 */
@Getter
public enum PurchaseDetailStatusEnum {


	NEW(0, "新建"),
	ASSIGNED(1, "已分配"),
	DOING(2, "正在采购"),
	OVER(3, "已完成"),
	FAILED(4, "采购失败");

	private Integer code;

	private String desc;

	PurchaseDetailStatusEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}


}
