package element.io.secskill.controller;

import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.mall.common.vo.SecKillVo;
import element.io.secskill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Slf4j
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

	@PostMapping(value = "/sec/kill", produces = "application/json;charset=utf-8")
	public String secKill(@RequestBody @Validated SecKillVo secKillVo) {
		log.info("接收到的参数{}", secKillVo);
		return service.createOrder(secKillVo);
	}

	
}
