package element.io.mall.common.msg;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-12-12
 */
@Data
public class SecKillOrderTo {

	private String orderSn;

	private Long skuId;

	private Integer count;

	private Long promotionSessionId;

	private BigDecimal secKillPrice;

	private Long memberId;

}
