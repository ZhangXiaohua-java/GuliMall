package element.io.mall.common.feign;

import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@FeignClient(value = "gulimall-cart", configuration = {CartConfig.class})
public interface CartRemoteFeignClient {

	@ResponseBody
	@GetMapping("/checked/items")
	R checkedItems();


}
