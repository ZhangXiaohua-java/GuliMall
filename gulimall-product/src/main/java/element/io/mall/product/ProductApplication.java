package element.io.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author 张晓华
 * @date 2022-10-27
 */
@EnableRedisHttpSession
@EnableCaching
@EnableFeignClients(basePackages = {"element.io.mall.common.service"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"element.io.mall.product", "element.io.mall.common.config"})
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class);
	}


}
