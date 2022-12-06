package element.io.mall.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Slf4j
@Configuration
public class CartConfig implements RequestInterceptor {


	@Override
	public void apply(RequestTemplate requestTemplate) {
		try {
			RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
			if (Objects.nonNull(attributes)) {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) attributes;
				HttpServletRequest request = requestAttributes.getRequest();
				String cookie = request.getHeader("Cookie");
				requestTemplate.header("Cookie", cookie);
				RequestContextHolder.setRequestAttributes(null);
			}
		} catch (Exception e) {
			log.error("无所谓的异常信息");
		}
	}


}
