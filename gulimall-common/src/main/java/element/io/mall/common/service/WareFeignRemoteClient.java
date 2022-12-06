package element.io.mall.common.service;

import element.io.mall.common.to.CourierTo;
import element.io.mall.common.to.StockLockTo;
import element.io.mall.common.to.WareSkuTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-12
 */
@FeignClient(value = "gulimall-ware")
public interface WareFeignRemoteClient {

	@PostMapping("/ware/waresku/stock/query")
	public R stockQuery(@RequestBody Long[] skuIds);

	@PostMapping("/ware/waresku/goods/stock")
	public List<WareSkuTo> goodsStock(@RequestBody List<Long> ids);

	@GetMapping("/ware/wareinfo/count/fee/{addId}")
	CourierTo countFee(@PathVariable("addId") Long addId);

	@ResponseBody
	@PostMapping("/ware/waresku/lock/stock")
	R lockStock(@RequestBody List<StockLockTo> tos);


}
