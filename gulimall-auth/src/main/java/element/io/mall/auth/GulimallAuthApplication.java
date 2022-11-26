package element.io.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = {"element.io"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"element.io"})
public class GulimallAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallAuthApplication.class, args);
	}


}
