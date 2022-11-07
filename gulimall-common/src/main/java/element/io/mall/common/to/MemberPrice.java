package element.io.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-11-6
 */
@Data
public class MemberPrice {

	private Long id;

	private String name;

	private BigDecimal price;

}
