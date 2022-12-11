package element.io.mall.coupon;


import element.io.mall.common.to.SeckillSessionTo;
import element.io.mall.coupon.entity.SeckillSkuRelationEntity;
import element.io.mall.coupon.service.SeckillSessionService;
import element.io.mall.coupon.service.impl.SeckillSkuRelationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallCouponApplicationTests {

	@Resource
	private SeckillSkuRelationServiceImpl service;

	@Resource
	private SeckillSessionService seckillSessionService;

	@Test
	public void contextLoads() {
		ArrayList<Long> list = new ArrayList<>();
		list.add(1601559322943815700L);
		list.add(1601560962367225900L);
		Map<Long, List<SeckillSkuRelationEntity>> map = service.batchQueryRelations(list);
		log.info("查询到的结果{}", map);
	}

	@Test
	public void seesionTest() {
		List<SeckillSessionTo> latestSecKillProducts = seckillSessionService.getLatestSecKillProducts();
		System.out.println(latestSecKillProducts);
	}

}
