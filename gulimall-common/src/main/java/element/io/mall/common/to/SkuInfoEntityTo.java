package element.io.mall.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * sku信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Data
public class SkuInfoEntityTo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long skuId;

	private BigDecimal price;
	
}
