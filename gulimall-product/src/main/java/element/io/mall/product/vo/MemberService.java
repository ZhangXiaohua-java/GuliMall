package element.io.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-11-6
 */
@Data
public class MemberService {

	private Long id;

	private String name;

	private BigDecimal price;

}
