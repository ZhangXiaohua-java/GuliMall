package element.io.mall.common.to;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
@Data
public class StockLockTo {

	// skuId
	private Long skuId;
	// 需要锁定的库存数量
	private Integer lockCount;

	// 订单标识
	private String orderSn;

}
