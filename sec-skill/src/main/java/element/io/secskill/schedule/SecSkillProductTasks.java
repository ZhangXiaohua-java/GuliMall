package element.io.secskill.schedule;

import element.io.mall.common.service.CouponRemoteClient;
import element.io.mall.common.service.ProductFeignRemoteClient;
import element.io.mall.common.to.SeckillSessionTo;
import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.mall.common.to.SkuInfoTo;
import element.io.mall.common.util.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.SecKillConstants.*;

/**
 * @author 张晓华
 * @date 2022-12-10
 */
@Slf4j
@Component
public class SecSkillProductTasks {

	@Resource
	private CouponRemoteClient couponRemoteClient;

	@Resource
	private ProductFeignRemoteClient productFeignRemoteClient;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private RedissonClient redissonClient;

	private static final String LOCK_NAME = "sec:kill:lock";

	@Scheduled(cron = "0 * * * * ?")
	public void putOnSecSkillProducts() {
		//	 查询所有秒杀活动信息
		RLock lock = redissonClient.getLock(LOCK_NAME);
		try {
			lock.lock();
			log.info("上锁");
			List<SeckillSessionTo> secKillProducts = couponRemoteClient.getLatest3DaysSecKillProducts();
			if (CollectionUtils.isEmpty(secKillProducts)) {
				return;
			}
			log.info("秒杀商品上架");
			saveSessionInfo(secKillProducts);
			saveSkus(secKillProducts);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			log.info("解锁");
		}

	}

	private void saveSessionInfo(List<SeckillSessionTo> seckillSessionTos) {
		seckillSessionTos.stream().forEach(ele -> {
			List<Long> skuIds = ele.getRelations().stream().map(SeckillSkuRelationTo::getSkuId).collect(Collectors.toList());
			ListOperations<String, Object> ops = redisTemplate.opsForList();
			long start = ele.getStartTime().getTime();
			long end = ele.getEndTime().getTime();
			String key = SEC_KILL_SESSIONS + start + "_" + end;
			if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
				ops.rightPush(key, skuIds);
			}
		});
	}


	private void saveSkus(List<SeckillSessionTo> seckillSessionTos) {
		BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SEC_KILL_SKUS);
		List<Long> skuIds = seckillSessionTos.stream()
				.flatMap(e -> e.getRelations().stream())
				.map(item -> item.getSkuId()).collect(Collectors.toList());
		Map<Long, SkuInfoTo> skuInfoMap = productFeignRemoteClient.batchQuerySkuInfo(skuIds);
		// 详细信息
		List<SeckillSkuRelationTo> list = seckillSessionTos.stream()
				.flatMap(ele -> ele.getRelations().stream())
				.map(element -> {
					SkuInfoTo info = skuInfoMap.get(element.getSkuId());
					element.setSkuInfo(info);
					SeckillSessionTo session = seckillSessionTos.stream().filter(i -> i.getId().equals(element.getPromotionSessionId())).findAny().get();
					element.setStart(session.getStartTime().getTime());
					element.setEnd(session.getEndTime().getTime());
					String uuid = CodeUtils.randomUUID();
					element.setToken(uuid);
					return element;
				}).collect(Collectors.toList());
		list.stream().forEach(el -> {
			String key = el.getPromotionSessionId().toString() + "_" + el.getSkuId();
			if (Boolean.FALSE.equals(ops.hasKey(key))) {
				ops.put(key, el);
				RSemaphore semaphore = redissonClient.getSemaphore(SEC_KILL_STOCK + el.getToken());
				semaphore.trySetPermits(el.getSeckillCount());
			}

		});

	}


}
