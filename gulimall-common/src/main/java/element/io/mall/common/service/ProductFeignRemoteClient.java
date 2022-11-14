package element.io.mall.common.service;

import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 张晓华
 * @date 2022-11-8
 */
@FeignClient(value = "gulimall-product")
public interface ProductFeignRemoteClient {

	@GetMapping("/product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);


}
