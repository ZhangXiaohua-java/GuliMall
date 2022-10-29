package element.io.mall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 张晓华
 * @date 2022-10-27
 */
@EnableDiscoveryClient
@SpringBootApplication
public class WareApplication {


	public static void main(String[] args) {
		SpringApplication.run(WareApplication.class);
	}

}
