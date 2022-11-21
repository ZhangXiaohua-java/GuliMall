package element.io.mall.common.service;

import element.io.mall.common.service.config.SmsAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@FeignClient(name = "sms", url = "https://gyytz.market.alicloudapi.com",
		configuration = {SmsAuthConfig.class})
public interface SmsRemoteClient {

	// 模板id a09602b817fd47e59e7c6e603d3f088d
	// smsSignId 2e65b1bb3d054466b82f0c9d125465e2
	// querys.put("param", "**code**:12345,**minute**:5");

	@PostMapping(value = "/sms/smsSend", produces = "application/json;charset=UTF-8")
	public String sendCode(@RequestParam(value = "mobile") String mobile,
						   @RequestParam(value = "param") String param,
						   @RequestParam(value = "smsSignId") String smsSignId,
						   @RequestParam(value = "templateId") String templateId);


}
