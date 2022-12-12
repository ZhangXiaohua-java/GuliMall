package element.io.secskill;

import element.io.mall.common.config.RedisHttpSessionConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.annotation.Resource;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = {"element.io.mall.common.service"})
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"element.io.mall.common.exhandler", "element.io.secskill", "element.io.mall.common.config"})
public class SecSkillApplication implements CommandLineRunner {

	@Resource
	private RedisHttpSessionConfig config;

	public static void main(String[] args) {
		SpringApplication.run(SecSkillApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		RedisSerializer serializer = config.redisSerializer();
		System.out.println(serializer);
	}
}
