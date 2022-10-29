package element.io.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 张晓华
 * @date 2022-10-27
 */
@EnableFeignClients(basePackages = {"element.io.mall.common.service"})
@EnableDiscoveryClient
@SpringBootApplication
public class MemberApplication {


	public static void main(String[] args) {
		SpringApplication.run(MemberApplication.class);
	}

}
