package element.io.mall.common.service;

import element.io.mall.common.to.SkuReductionTo;
import element.io.mall.common.to.SpuBoundTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 张晓华
 * @date 2022-10-28
 */
@FeignClient(name = "gulimall-coupon", path = "/coupon")
public interface CouponRemoteClient {

	@PostMapping("/spubounds/detail/save")
	R saveBoundInfo(@RequestBody SpuBoundTo spuBoundTo);


	@PostMapping("/skufullreduction/reduction/save")
	R saveReduction(@RequestBody SkuReductionTo skuReductionTo);


}
