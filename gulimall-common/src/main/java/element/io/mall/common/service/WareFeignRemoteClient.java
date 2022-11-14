package element.io.mall.common.service;

import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 张晓华
 * @date 2022-11-12
 */
@FeignClient(name = "gulimall-ware")
public interface WareFeignRemoteClient {

	@PostMapping("/ware/waresku/stock/query")
	public R stockQuery(@RequestBody Long[] skuIds);

}
