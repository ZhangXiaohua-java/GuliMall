package element.io.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 张晓华
 * @date 2022-10-27
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"element.io.mall.common.exhandler", "element.io.mall.product"})
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class);
	}


}
