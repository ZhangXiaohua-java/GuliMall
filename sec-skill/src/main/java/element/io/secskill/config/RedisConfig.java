package element.io.secskill.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import element.io.mall.common.serializer.FastJsonSerializer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author 张晓华
 * @date 2022-12-10
 */
@Configuration
public class RedisConfig {


	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		RedisSerializer<String> string = RedisSerializer.string();
		redisTemplate.setKeySerializer(string);
		redisTemplate.setHashKeySerializer(string);
		FastJsonSerializer<Object> jsonSerializer = new FastJsonSerializer<>();
		redisTemplate.setValueSerializer(jsonSerializer);
		redisTemplate.setHashValueSerializer(jsonSerializer);
		return redisTemplate;
	}


	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
				.setAddress("redis://192.168.10.102:6379")
				.setPassword("redis")
				.setDatabase(0);
		return Redisson.create(config);
	}

	@Bean
	public HttpMessageConverters httpMessageConverters() {
		FastJsonHttpMessageConverter messageConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig config = new FastJsonConfig();
		config.setCharset(Charset.forName("utf-8"));
		config.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteMapNullValue);
		config.setDateFormat("yyyy-MM-dd HH:mm:ss");
		messageConverter.setFastJsonConfig(config);
		messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return new HttpMessageConverters(messageConverter);
	}

}
