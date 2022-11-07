package element.io.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-11-6
 */
@Data
public class SpuBoundTo {

	private Long spuId;

	private BigDecimal buyBounds;

	private BigDecimal growBounds;
	

}
