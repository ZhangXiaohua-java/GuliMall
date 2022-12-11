package element.io.mall.common.service;

import element.io.mall.common.to.SkuInfoEntityTo;
import element.io.mall.common.to.SkuInfoTo;
import element.io.mall.common.to.SpuInfoTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 张晓华
 * @date 2022-11-8
 */
@FeignClient(value = "gulimall-product")
public interface ProductFeignRemoteClient {

	@GetMapping("/product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);

	@GetMapping("/product/skusaleattrvalue/sale/attrs/list/{skuId}")
	public List<String> saleAttr(@PathVariable("skuId") Long skuId);

	@ResponseBody
	@PostMapping("/product/skuinfo/batch/query/price")
	public List<SkuInfoEntityTo> batchQuerySkuPrice(@RequestBody List<Long> ids);


	@ResponseBody
	@GetMapping("/product/spuinfo/spu/{skuId}")
	SpuInfoTo querySpuInfoBySkuId(@PathVariable("skuId") Long skuId);


	@ResponseBody
	@PostMapping("/product/skuinfo/batch/query/sku/info")
	Map<Long, SkuInfoTo> batchQuerySkuInfo(@RequestBody List<Long> skuIds);

}
