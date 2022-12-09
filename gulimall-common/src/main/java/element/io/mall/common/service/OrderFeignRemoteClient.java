package element.io.mall.common.service;

import element.io.mall.common.to.OrderStatusTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@FeignClient(value = "gulimall-order")
public interface OrderFeignRemoteClient {


	@ResponseBody
	@GetMapping("/order/status/{orderSn}")
	OrderStatusTo orderStatus(@PathVariable("orderSn") String orderSn);

}
