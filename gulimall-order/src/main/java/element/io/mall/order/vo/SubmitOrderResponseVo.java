package element.io.mall.order.vo;

import element.io.mall.order.entity.OrderItemEntity;
import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
@Data
public class SubmitOrderResponseVo {

	private OrderItemEntity order;

	// 0 success -1 token验证失败
	private Integer code;

	private String orderSn;

}
