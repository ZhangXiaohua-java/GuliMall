package element.io.mall.auth.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-14
 */
public class FastJsonSerializer<T> implements RedisSerializer<T> {

	private final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private Class<T> clazz;

	public FastJsonSerializer() {
	}

	public FastJsonSerializer(Class<T> clazz) {
		this.clazz = clazz;
	}


	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (Objects.isNull(t)) {
			return new byte[0];
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (Objects.isNull(bytes) || bytes.length == 0) {
			return null;
		}
		String str = new String(bytes, DEFAULT_CHARSET);
		return (T) JSON.parseObject(str, clazz);
	}


}
