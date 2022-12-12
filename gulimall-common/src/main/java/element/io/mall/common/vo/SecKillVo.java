package element.io.mall.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Data
public class SecKillVo {

	@NotBlank(message = "口令不能为空")
	private String token;

	@NotNull(message = "请指定要抢购的商品的数量")
	private Integer count;

	@NotNull(message = "商品的id不能为空")
	private Long skuId;

	@NotNull(message = "秒杀活动的id不能为空")
	private Long promotionSessionId;
	

}
