package element.io.mall.common.service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@Configuration
public class SmsAuthConfig implements RequestInterceptor {

	@Value("${sms.appcode}")
	private String appcode;


	@Override
	public void apply(RequestTemplate requestTemplate) {
		requestTemplate.header("Authorization", "APPCODE " + appcode);
	}


}
