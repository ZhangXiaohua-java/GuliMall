package element.io.search.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-10
 */
@SuppressWarnings({"all"})
@Configuration
public class WebComponentConfig {

	
	private FastJsonConfig fastJsonConfig() {
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		fastJsonConfig.setCharset(Charset.forName("UTF-8"));
		List<SerializerFeature> featureList = new ArrayList<>();
		featureList.add(SerializerFeature.WriteEnumUsingName);
		featureList.add(SerializerFeature.WriteMapNullValue);
		featureList.add(SerializerFeature.WriteNullListAsEmpty);
		featureList.add(SerializerFeature.WriteDateUseDateFormat);
		featureList.add(SerializerFeature.WriteNullStringAsEmpty);
		featureList.add(SerializerFeature.WriteNullNumberAsZero);
		fastJsonConfig.setSerializerFeatures(featureList.toArray(new SerializerFeature[]{}));
		return fastJsonConfig;
	}

}
