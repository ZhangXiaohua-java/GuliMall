package element.io.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = {"element.io.mall.common.service"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"element.io.mall.auth", "element.io.mall.common.config"})
public class GulimallAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallAuthApplication.class, args);
	}


}
