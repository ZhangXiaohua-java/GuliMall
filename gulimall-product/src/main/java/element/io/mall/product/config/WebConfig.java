package element.io.mall.product.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import element.io.mall.common.util.ThreadUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张晓华
 * @date 2022-10-30
 */
@Configuration
public class WebConfig {

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConvert() {
		FastJsonHttpMessageConverter messageConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty
		);
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		messageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
		messageConverter.setFastJsonConfig(fastJsonConfig);
		return new HttpMessageConverters(messageConverter);
	}

	@Bean
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		FastJsonSerializer<Object> fastJsonSerializer = new FastJsonSerializer<>(Object.class);
		ParserConfig.getGlobalInstance().setAsmEnable(true);
		RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(fastJsonSerializer);
		redisTemplate.setHashValueSerializer(fastJsonSerializer);
		return redisTemplate;
	}


	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
				.setAddress("redis://192.168.56.3:6379")
				.setPassword("redis")
				.setDatabase(0);
		// 使用EPOLL会一直报错,java.lang.ClassNotFound什么玩意...
		//config.setTransportMode(TransportMode.EPOLL);
		return Redisson.create(config);
	}

	/**
	 * 在自定义了RedisCacheConfiguration配置对象后,会导致配置文件中spring.cache的配置不生效,所以还需要手动设置
	 * 主要就是修改Spring Cache的序列化方式
	 *
	 * @param cacheProperties 注入的缓存配置
	 * @return Redis的缓存配置信息
	 */
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
		CacheProperties.Redis redisProperties = cacheProperties.getRedis();
		FastJsonSerializer<Object> jsonSerializer = new FastJsonSerializer<>();
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		return config;
	}


	@Bean
	public ThreadPoolExecutor productThreadPool(ThreadConfig threadConfig) {
		return ThreadUtils.getThreadPool(threadConfig.getCore(), threadConfig.getMax(),
				threadConfig.getTimeout(), threadConfig.getCapacity());
	}


}
