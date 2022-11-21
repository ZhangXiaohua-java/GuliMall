package element.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import element.io.mall.common.service.SmsRemoteClient;
import element.io.mall.common.to.SmsDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {

	@Resource
	private SmsRemoteClient smsRemoteClient;

	@Test
	public void testSendCode() {
		String res = smsRemoteClient.sendCode("19937656750",
				"**code**:52100,**minute**:15",
				"2e65b1bb3d054466b82f0c9d125465e2",
				"a09602b817fd47e59e7c6e603d3f088d"
		);
		SmsDto dto = JSON.parseObject(res, new TypeReference<SmsDto>() {
		});
		log.info("调用的响应结果:{}", dto);
	}


}
