package element.io.mall.common.enumerations;

import lombok.Getter;

/**
 * @author 张晓华
 * @date 2022-11-3
 */
@Getter
public enum AttrType {

	BASE_ATTR(1, "基本属性"),
	SALE_ATTR(0, "销售属性");

	AttrType(Integer code, String symbol) {
		this.code = code;
		this.symbol = symbol;
	}

	private Integer code;

	private String symbol;

}
