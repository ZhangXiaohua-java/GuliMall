package element.io.mall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 张晓华
 * @date 2022-10-27
 */
@EnableFeignClients(basePackages = {"element.io.mall.common.service", "element.io.mall.common.feign"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"element.io.mall.common.config", "element.io.mall.order"})
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class);
	}

}
