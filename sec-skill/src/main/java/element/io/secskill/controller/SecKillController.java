package element.io.secskill.controller;

import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.secskill.service.SecKillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@RestController
public class SecKillController {

	@Resource
	private SecKillService service;

	@GetMapping("/curr/sec/kill")
	public List<SeckillSkuRelationTo> queryCurrentSecKillProducts() {
		return service.queryCuurentSecKillProducts();
	}

	@GetMapping("/curr/isSec/{skuId}")
	public SeckillSkuRelationTo isSecKillProduct(@PathVariable Long skuId) {
		return service.isInSecKill(skuId);
	}

}
