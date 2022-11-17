package element.io.mall.product;


import cn.hutool.core.io.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

	@Resource
	private RedissonClient redissonClient;

	@Test
	public void checkRedisson() {
		System.out.println(redissonClient.toString());
	}


	@Test
	public void readFile() throws FileNotFoundException {
		File file = FileUtil.file("delete.lua");
		String str = FileUtil.readString(file, StandardCharsets.UTF_8);
		System.out.println(str);
	}


}
