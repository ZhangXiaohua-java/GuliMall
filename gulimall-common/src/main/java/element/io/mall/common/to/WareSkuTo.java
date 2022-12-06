package element.io.mall.common.to;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
@Data
public class WareSkuTo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long skuId;

	private Integer stock;

	private boolean hasStock;

}
