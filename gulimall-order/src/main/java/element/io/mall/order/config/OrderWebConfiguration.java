package element.io.mall.order.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import element.io.mall.common.serializer.FastJsonSerializer;
import element.io.mall.common.util.ThreadUtils;
import element.io.mall.order.component.PermissionInterceptor;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Configuration
public class OrderWebConfiguration implements WebMvcConfigurer {

	@Resource
	private PermissionInterceptor permissionInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
	}

	@Bean
	public ThreadPoolExecutor threadPoolExecutor() {
		return ThreadUtils.getThreadPool(5, 20, 60, 10);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashValueSerializer(stringRedisSerializer);
		FastJsonSerializer<Object> jsonSerializer = new FastJsonSerializer<>();
		redisTemplate.setValueSerializer(jsonSerializer);
		redisTemplate.setHashValueSerializer(jsonSerializer);
		return redisTemplate;
	}

	@Bean
	public HttpMessageConverters fastJsonMessageConverts() {
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		FastJsonConfig config = new FastJsonConfig();
		config.setDateFormat("yyyy-MM-dd HH:mm:ss");
		config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.PrettyFormat,
				SerializerFeature.IgnoreNonFieldGetter);
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		converter.setFastJsonConfig(config);
		return new HttpMessageConverters(converter);
	}


}
