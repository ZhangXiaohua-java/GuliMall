package element.io.mall.common.serializer;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-14
 */
public class StringRedisSerializer implements RedisSerializer<Object> {

	private final Charset charset;

	private final String target = "\"";

	private final String replacement = "";

	public StringRedisSerializer() {
		this.charset = Charset.forName("UTF-8");
	}

	public StringRedisSerializer(Charset charset) {
		this.charset = charset;
	}


	@Override

	public byte[] serialize(Object o) throws SerializationException {
		String string = JSON.toJSONString(o);
		if (Objects.isNull(string)) {
			return null;
		}
		string = string.replace(target, replacement);
		return string.getBytes(charset);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return Objects.nonNull(bytes) && bytes.length != 0 ? new String(bytes, charset) : null;
	}
}
