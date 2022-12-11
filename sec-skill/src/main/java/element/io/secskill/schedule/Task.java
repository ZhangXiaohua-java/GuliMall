package element.io.secskill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-12-9
 */
@SuppressWarnings({"all"})
@Slf4j
@Component
public class Task {


	@Async("poll")
	//@Scheduled(cron = "* * * * * ?")
	public void hello() throws IOException, InterruptedException {
		log.info("hello");
		FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\兰心雅我老婆.json"), true);
		String content = "你爱我,我爱你,咱俩一起甜蜜蜜 \r\n";
		fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
		TimeUnit.SECONDS.sleep(3);
	}

	
}
