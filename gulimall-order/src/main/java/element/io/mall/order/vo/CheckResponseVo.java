package element.io.mall.order.vo;

import element.io.mall.common.to.CartItemTo;
import element.io.mall.common.to.MemberReceiveAddressTo;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Data
public class CheckResponseVo {

	// 收货地址
	private List<MemberReceiveAddressTo> receiveAddressTos;

	// 可用积分优惠信息
	private Integer integration;


	private List<CartItemTo> items;

	private BigDecimal total;

	private BigDecimal pay;

	// 防重复提交
	private String orderToken;

	private Integer count;

	private Map<Long, Boolean> stockStatus;

	public BigDecimal getTotal() {
		return items.stream()
				.map(e -> new BigDecimal(e.getCount()).multiply(e.getPrice()))
				.reduce(new BigDecimal("0"), (pre, curr) -> pre = pre.add(curr));
	}


	public BigDecimal getPay() {
		if (Objects.nonNull(this.integration)) {
			// 1000 成长值抵扣一元
			return getTotal().subtract(new BigDecimal(integration).divide(new BigDecimal("1000"), RoundingMode.DOWN));
		}
		return this.getTotal();
	}

	public Integer getCount() {
		return items.stream().map(i -> i.getCount())
				.reduce(0, (pre, curr) -> pre = pre + curr);
	}


}
