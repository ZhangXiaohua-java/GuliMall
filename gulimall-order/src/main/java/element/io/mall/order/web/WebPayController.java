package element.io.mall.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.niezhiliang.simple.pay.dto.AlipayPcPayDTO;
import element.io.mall.common.util.HashObjectMapper;
import element.io.mall.order.service.OrderService;
import element.io.mall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张晓华
 * @date 2022-12-8
 */
@SuppressWarnings({"all"})
@Slf4j
@Controller
public class WebPayController {

	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmQCrB4aGcm28zFf3DV0cqaSKJzGm8i05F0klUhHqFXtJoZ2/JKZ1FtCcJbZMDr5jVCzbiEnEE47rTP+0WYDqqZz3DiOQILr/gyMWZFkjR2V0RdoeKvIi3W7bDTpEkkCsOSEZtZA3SRnEvdj1rfCGLPr74L23civyxnAawFLFm1ObVUWyatZ5rLswfmg2nK1rOSF5Cx+3LeJydXDkk7oGaum+SJe0k4Z34AijMUjLZMLBezDvNMeHfe/Cn2K+rChFCpOc3kv5Jfz7HES30hH4aweLiCFgd9EYFOVcZiz1MKqc2vt22XUEvbsohlOJHU9HCy4aj+qVDT91R3bzODBtMwIDAQAB";


	@Resource
	private OrderService orderService;

	@Resource
	private LoadBalancerClient loadBalancerClient;


	@GetMapping("/notify/callback")
	public void callback(Object obj) {
		log.info("回调接收到的消息{}", obj);
	}


	@GetMapping(value = "/toPay/{orderSn}")
	public String toPay(@PathVariable String orderSn) throws AlipayApiException, InvocationTargetException, IllegalAccessException, UnsupportedEncodingException {
		// 被逼无奈,鬼知道这个服务中集成支付宝老是出错,空指针啥的就暂时不做处理了,不想写页面了
		AlipayPcPayDTO payDTO = orderService.getOrderInfoByOrderSn(orderSn);
		ServiceInstance serviceInstance = loadBalancerClient.choose("guli-pay");
		String scheme = serviceInstance.getScheme();
		String host = serviceInstance.getHost();
		int port = serviceInstance.getPort();
		if (!StringUtils.hasText(scheme)) {
			scheme = "http";
		}
		String url = scheme + "://" + host + ":" + port + "/pc/pay";
		HashObjectMapper<AlipayPcPayDTO> mapper = new HashObjectMapper<>();
		Map<String, Object> map = mapper.mappingObjectToMap(payDTO);
		String[] array = map.keySet().stream()
				.map(String::toString).collect(Collectors.toList())
				.toArray(new String[]{});
		String params = "";
		for (int i = 0; i < array.length; i++) {
			if (i == 0) {
				params = params.concat("?" + array[i] + "=" + map.get(array[i]).toString());
			} else {
				params = params.concat("&" + array[i] + "=" + map.get(array[i]).toString());
			}
		}
		//params = URLEncoder.encode(params, "utf-8");
		url = url + params;
		log.info("拼接出的url{}", url);
		return "redirect:" + url;
	}


	@ResponseBody
	@PostMapping("/notify/ali")
	public String callback(HttpServletRequest request) throws AlipayApiException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Map<String, String> params = new HashMap();
		Map<Object, Object> entries = new HashMap();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
			entries.put(name, valueStr);
		}
		boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "utf-8", "RSA2");
		if (!verify_result) {
			throw new RuntimeException("校验失败,签名不一致");
		} else {
			// 更新商品的状态信息
			log.info("接收到了支付宝的通知消息{}", params);
			// 自己之前写的一个利用反射的工具类,效率还可以
			PayAsyncVo payAsyncVo = new HashObjectMapper<PayAsyncVo>().mappingToObject(entries, PayAsyncVo.class);
			log.info("对象信息{}", payAsyncVo);
			if (!orderService.updateOrderStatus(payAsyncVo)) {
				throw new RuntimeException("DB异常");
			}
		}
		return "success";
	}


}
