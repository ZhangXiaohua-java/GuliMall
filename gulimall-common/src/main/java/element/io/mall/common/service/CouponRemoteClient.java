package element.io.mall.common.service;

import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 张晓华
 * @date 2022-10-28
 */
@FeignClient(name = "gulimall-coupon", path = "/coupon/coupon")
public interface CouponRemoteClient {

	@GetMapping("/coupons")
	R queryCoupons();


}
