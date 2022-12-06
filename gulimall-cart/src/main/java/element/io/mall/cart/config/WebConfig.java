package element.io.mall.cart.config;

import element.io.mall.cart.component.UserInfoInterceptor;
import element.io.mall.common.serializer.FastJsonSerializer;
import element.io.mall.common.util.ThreadUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张晓华
 * @date 2022-11-27
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Resource
	private UserInfoInterceptor userInfoInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userInfoInterceptor).addPathPatterns("/**");
	}


	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
		FastJsonSerializer<Object> jsonSerializer = new FastJsonSerializer<>();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(jsonSerializer);
		redisTemplate.setHashValueSerializer(jsonSerializer);
		return redisTemplate;
	}

	@Bean
	public ThreadPoolExecutor executor() {
		return ThreadUtils.getThreadPool(3, 10, 60, 10);
	}

}
