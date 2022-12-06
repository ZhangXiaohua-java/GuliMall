package element.io.mall.cart.controller;

import element.io.mall.cart.component.UserInfoContext;
import element.io.mall.cart.service.CartService;
import element.io.mall.cart.vo.CartEntity;
import element.io.mall.cart.vo.CartItem;
import element.io.mall.cart.vo.UserInfo;
import element.io.mall.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Slf4j
@Controller
public class CartController {

	@Resource
	private CartService cartService;

	@GetMapping("/cart.html")
	public String cartList(Model model) {
		UserInfo userInfo = UserInfoContext.currentContext().get();
		log.info("访问当前资源的用户信息{}", userInfo);
		CartEntity cart = cartService.queryUserCartInfo();
		log.info("查询到的结果{}", cart);
		model.addAttribute("cart", cart);
		return "cartList";
	}


	@GetMapping("/cart/add/{skuId}/{count}")
	public String addItemToCart(@PathVariable Long skuId, @PathVariable Integer count, Model model) throws ExecutionException, InterruptedException {
		if (count <= 0) {
			// 暂时不想做处理,提交的参数Nan问题也不考虑
			throw new RuntimeException("非法参数");
		}
		cartService.addItem(skuId, count);
		return "redirect:http://cart.gulimall.com/recent.html?skuId=" + skuId;
	}

	@GetMapping("/recent.html")
	public String itemShow(@RequestParam Long skuId, Model model) {
		CartItem cartItem = cartService.queryRecentAddedItem(skuId);
		model.addAttribute("item", cartItem);
		return "success";
	}

	@ResponseBody
	@DeleteMapping("/delete/{skuId}")
	public R deleteItem(@PathVariable Long skuId) {
		if (cartService.deleteItem(skuId)) {
			return R.ok();
		} else {
			return R.error();
		}
	}

	@ResponseBody
	@PutMapping("/update/check/{skuId}")
	public R updateItemStatus(@PathVariable Long skuId, Integer status) {
		cartService.updateItemCheckStatus(skuId, status);
		return R.ok();
	}

	@ResponseBody
	@PostMapping("/update/count/{skuId}")
	public R updateItemCount(@PathVariable Long skuId, Integer count) {
		cartService.updateItemCount(skuId, count);
		return R.ok();
	}

	@ResponseBody
	@GetMapping(value = "/checked/items", produces = "application/json;charset=utf-8")
	public R checkedItems() {
		return R.ok().put("data", cartService.getUserCheckedItems());
	}


}
