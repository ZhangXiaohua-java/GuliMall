package element.io.mall.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Data
public class CartItem implements Serializable {

	private Long skuId;

	private String title;

	private String image;

	private BigDecimal price;

	private Integer count;

	private BigDecimal total;

	private Boolean checked;

	private List<String> saleAttrs;

	public BigDecimal getTotal() {
		return this.price.multiply(new BigDecimal(String.valueOf(this.count)));
	}


	public Boolean getChecked() {
		return Objects.isNull(this.checked) ? true : this.checked;
	}

}
