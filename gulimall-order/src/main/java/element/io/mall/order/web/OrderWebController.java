package element.io.mall.order.web;

import element.io.mall.common.to.OrderStatusTo;
import element.io.mall.common.util.R;
import element.io.mall.order.service.OrderService;
import element.io.mall.order.vo.CheckResponseVo;
import element.io.mall.order.vo.OrderRequestVo;
import element.io.mall.order.vo.OrderWithOrderItemVo;
import element.io.mall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@Slf4j
@Controller
public class OrderWebController {

	@Resource
	private OrderService orderService;

	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ExecutionException, InterruptedException {
		CheckResponseVo vo = orderService.checkToPay();
		model.addAttribute("data", vo);
		return "pay";
	}


	@ResponseBody
	@PostMapping("/submit/order")
	public R createOrder(@RequestBody OrderRequestVo vo) throws IOException {
		log.info("接收到的数据{}", vo);
		try {
			SubmitOrderResponseVo responseVo = orderService.createOrder(vo);
			String orderSn = responseVo.getOrderSn();
			if (responseVo.getCode() == 0) {
				return R.ok().put("orderSn", orderSn);
			} else {
				String msg = "msg=口令已经失效";
				return R.error(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "msg=商品库存不足";
			return R.error(msg);
		}

	}

	@ResponseBody
	@GetMapping("/order/status/{orderSn}")
	public OrderStatusTo orderStatus(@PathVariable("orderSn") String orderSn) {
		return orderService.queryOrderStatus(orderSn);
	}


	@GetMapping("/list.html")
	public String orderList(Model model, @RequestParam(defaultValue = "1") Integer pageNum,
							@RequestParam(defaultValue = "10") Integer pageSize) {
		List<OrderWithOrderItemVo> vos = orderService.queryUserOrders(pageNum, pageSize);
		model.addAttribute("orders", vos);
		log.info("查询到的数据{}", vos);
		return "list";
	}

	@GetMapping("/money.html")
	public String money() {
		return "money";
	}
	

}
