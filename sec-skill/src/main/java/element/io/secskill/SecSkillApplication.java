package element.io.secskill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients(basePackages = {"element.io.mall.common.service"})
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SecSkillApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecSkillApplication.class, args);
	}

}
