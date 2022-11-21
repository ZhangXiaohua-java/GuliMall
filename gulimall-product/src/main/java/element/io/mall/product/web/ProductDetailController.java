package element.io.mall.product.web;

import element.io.mall.product.service.SkuInfoService;
import element.io.mall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author 张晓华
 * @date 2022-11-20
 */
@Controller
public class ProductDetailController {

	@Resource
	private SkuInfoService skuInfoService;

	@GetMapping("/{skuId}.html")
	public String item(@PathVariable Long skuId, Model model) throws ExecutionException, InterruptedException {
		SkuItemVo skuItemVo = skuInfoService.getSkuDetailInfo(skuId);
		model.addAttribute("data", skuItemVo);
		return "item";
	}


}
