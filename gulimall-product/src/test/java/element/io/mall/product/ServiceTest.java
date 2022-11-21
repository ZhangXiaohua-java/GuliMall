package element.io.mall.product;

import element.io.mall.product.service.SkuInfoService;
import element.io.mall.product.vo.SkuItemVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author 张晓华
 * @date 2022-11-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

	@Resource
	private SkuInfoService skuInfoService;


	@Test
	public void testSql() throws ExecutionException, InterruptedException {
		SkuItemVo itemVo = skuInfoService.getSkuDetailInfo(30L);
		System.out.println(itemVo);
	}

}
