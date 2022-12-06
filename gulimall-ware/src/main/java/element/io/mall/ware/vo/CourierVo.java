package element.io.mall.ware.vo;

import element.io.mall.common.to.MemberReceiveAddressTo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Data
public class CourierVo {

	private MemberReceiveAddressTo address;

	private BigDecimal price;

}
