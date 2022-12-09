package element.io.mall.pay.controller;

import com.niezhiliang.simple.pay.dto.AlipayPcPayDTO;
import com.niezhiliang.simple.pay.utils.PayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 张晓华
 * @date 2022-12-8
 */
@Controller
public class WebPayController {


	@ResponseBody
	@GetMapping("/pc/pay")
	public String pay(AlipayPcPayDTO alipayPcPayDTO) throws UnsupportedEncodingException {
		String decode = URLDecoder.decode(alipayPcPayDTO.getSubject(), "utf-8");
		alipayPcPayDTO.setSubject(decode);
		String body = URLDecoder.decode(alipayPcPayDTO.getBody(), "utf-8");
		alipayPcPayDTO.setBody(body);
		return PayUtils.alpayPcPay(alipayPcPayDTO);
	}


}
