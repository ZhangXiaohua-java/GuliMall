package element.io.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Data
public class CourierTo {

	private MemberReceiveAddressTo address;

	private BigDecimal price;

}
