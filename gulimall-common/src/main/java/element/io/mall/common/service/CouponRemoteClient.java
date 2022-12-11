package element.io.mall.common.service;

import element.io.mall.common.to.SeckillSessionTo;
import element.io.mall.common.to.SkuReductionTo;
import element.io.mall.common.to.SpuBoundTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

	@ResponseBody
	@GetMapping("/seckillsession/latest/sec/kill")
	List<SeckillSessionTo> getLatest3DaysSecKillProducts();


}
