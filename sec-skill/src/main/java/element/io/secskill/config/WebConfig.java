package element.io.secskill.config;

import element.io.secskill.component.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Resource
	private PermissionInterceptor permissionInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(permissionInterceptor).addPathPatterns("/sec/kill");
	}


}
