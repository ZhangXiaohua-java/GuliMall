package element.io.mall.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Data
public class CartItemTo implements Serializable {

	private Long skuId;

	private String title;

	private String image;

	private BigDecimal price;

	private Integer count;

	private BigDecimal total;

	private List<String> saleAttrs;

}
