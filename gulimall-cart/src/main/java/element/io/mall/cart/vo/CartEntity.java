package element.io.mall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Data
public class CartEntity {

	// 购物项
	private List<CartItem> items;

	// 商品总数量
	private Integer totalCount;

	// 商品种类个数
	private Integer typeCount;

	// 购物车内商品累计总价
	private BigDecimal total;

	// 所有优惠券的可减免价格
	private BigDecimal reduction;

	public Integer getTotalCount() {
		if (CollectionUtils.isEmpty(this.items)) {
			return 0;
		}
		return this.items.stream()
				.filter(e -> e.getChecked())
				.map(CartItem::getCount)
				.reduce(0, (pre, curr) -> pre += curr);
	}

	public Integer getTypeCount() {
		return this.items.size();
	}

	public BigDecimal getTotal() {
		BigDecimal total = this.items.stream()
				.filter(CartItem::getChecked)
				.map(item -> item.getPrice().multiply(new BigDecimal(String.valueOf(item.getCount()))))
				.reduce(new BigDecimal("0"), BigDecimal::add);
		if (this.reduction != null) {
			total = total.subtract(this.reduction);
		}
		return total;
	}


}
