package element.io.mall.common.config;

import element.io.mall.common.serializer.FastJsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Configuration
public class RedisHttpSessionConfig {

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setDomainName("gulimall.com");
		cookieSerializer.setCookieName("cook");
		return cookieSerializer;
	}


	@SuppressWarnings({"rawtypes"})
	@Bean(name = "springSessionDefaultRedisSerializer")
	public RedisSerializer redisSerializer() {
		return new FastJsonSerializer();
	}

}
