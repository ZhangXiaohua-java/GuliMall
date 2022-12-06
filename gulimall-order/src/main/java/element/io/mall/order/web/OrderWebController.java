package element.io.mall.order.web;

import element.io.mall.common.util.R;
import element.io.mall.order.service.OrderService;
import element.io.mall.order.vo.CheckResponseVo;
import element.io.mall.order.vo.OrderRequestVo;
import element.io.mall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
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

	@GetMapping("/{page}.html")
	public String access(@PathVariable String page) {
		return page;
	}

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
			if (responseVo.getCode() == 0) {
				return R.ok();
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


}
