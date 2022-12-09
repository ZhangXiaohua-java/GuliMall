package element.io.mall.common.to;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Data
public class OrderStatusTo {

	private String orderSn;

	/**
	 * 订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
	 */
	private Integer status;

}
