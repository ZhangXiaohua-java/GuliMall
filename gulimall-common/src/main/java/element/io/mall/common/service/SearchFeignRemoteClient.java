package element.io.mall.common.service;

import element.io.mall.common.to.SkuEsModelTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@FeignClient(name = "gulimall-search")
public interface SearchFeignRemoteClient {

	@PostMapping("/es/storage")
	R storageSkuInfo(@RequestBody List<SkuEsModelTo> skuEsModelTos);

}
