package element.io.mall.order.vo;

import element.io.mall.order.entity.OrderEntity;
import element.io.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
@Data
public class OrderVo {

	private OrderEntity order;

	private List<OrderItemEntity> items;

	private BigDecimal pay;

	private BigDecimal fare;


}
