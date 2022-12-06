package element.io.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
@Data
public class OrderRequestVo {

	private Long addrId;

	private String orderToken;

	private BigDecimal needPay;
	
}
