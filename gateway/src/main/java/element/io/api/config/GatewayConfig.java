package element.io.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author 张晓华
 * @date 2022-10-29
 */
@Configuration
public class GatewayConfig {

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://127.0.0.1:8001");
		configuration.addAllowedOrigin("http://localhost:8001");
		configuration.addAllowedOrigin("http://www.gulimall.com");
		configuration.addAllowedOrigin("http://order.gulimall.com");
		configuration.addAllowedOrigin("http://item.gulimall.com");
		configuration.addAllowedOrigin("http://auth.gulimall.com");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setMaxAge(3600L);
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
		configurationSource.registerCorsConfiguration("/**", configuration);
		return new CorsWebFilter(configurationSource);
	}


}
