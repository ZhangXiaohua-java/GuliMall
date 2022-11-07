package element.io.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-6
 */
@Data
public class SkuReductionTo {

	private Long skuId;

	private BigDecimal fullCount;

	private BigDecimal discount;

	private int countStatus;

	private BigDecimal fullPrice;

	private BigDecimal reducePrice;

	private int priceStatus;

	private List<MemberPrice> memberPrice;


}
