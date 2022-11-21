package element.io.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@ConfigurationProperties("product.thread")
@Component
@Data
public class ThreadConfig {

	private Integer core;

	private Integer max;

	private Integer timeout;

	private Integer capacity;


}
