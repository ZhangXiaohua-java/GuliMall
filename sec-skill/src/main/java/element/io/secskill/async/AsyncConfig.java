package element.io.secskill.async;

import element.io.mall.common.util.ThreadUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张晓华
 * @date 2022-12-9
 */
@SuppressWarnings({"all"})
@Configuration
public class AsyncConfig {


	@Bean("poll")
	public ThreadPoolExecutor threadPoolExecutor() {
		return ThreadUtils.getThreadPool(9, 99, 99, 99);
	}


}

