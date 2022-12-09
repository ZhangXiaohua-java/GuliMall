package element.io.mall.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GuliPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuliPayApplication.class, args);
	}

}
