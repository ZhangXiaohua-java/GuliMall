package element.io.mall.order.vo;

import element.io.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-8
 */
@Data
public class OrderWithOrderItemVo {

	private String orderSn;

	private Date createTime;

	private String receiverName;

	private BigDecimal pay;

	private Integer count;

	private String status;
	

	private List<OrderItemEntity> items;

}
