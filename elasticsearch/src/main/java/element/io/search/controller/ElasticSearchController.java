package element.io.search.controller;

import element.io.mall.common.to.SkuEsModelTo;
import element.io.mall.common.util.R;
import element.io.search.service.IEsStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@RestController()
public class ElasticSearchController {

	@Resource
	private IEsStorageService storageService;

	@PostMapping("/es/storage")
	public R storageSkuInfo(@RequestBody List<SkuEsModelTo> skuEsModelTos) throws IOException {
		if (storageService.storageSkuInfo(skuEsModelTos)) {
			return R.ok();
		} else {
			return R.error();
		}
	}


}
