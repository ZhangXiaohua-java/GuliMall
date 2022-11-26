package element.io.sso.client;

import element.io.sso.client.interceptor.ResourceInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@SpringBootApplication
public class SsoClientApplication implements WebMvcConfigurer {

	@Resource
	private ResourceInterceptor resourceInterceptor;

	public static void main(String[] args) {
		SpringApplication.run(SsoClientApplication.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(resourceInterceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico")
				.excludePathPatterns("/error");
	}
}
