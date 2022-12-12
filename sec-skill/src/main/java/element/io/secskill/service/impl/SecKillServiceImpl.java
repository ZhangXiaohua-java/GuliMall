package element.io.secskill.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.TypeReference;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.msg.SecKillOrderTo;
import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.mall.common.util.CodeUtils;
import element.io.mall.common.util.DataUtil;
import element.io.mall.common.vo.SecKillVo;
import element.io.secskill.component.PermissionInterceptor;
import element.io.secskill.service.SecKillService;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.MQConstants.ORDER_EVENT_EXCHANGE;
import static element.io.mall.common.enumerations.MQConstants.SEC_KILL_QUEUE_ORDER_BINDING;
import static element.io.mall.common.enumerations.SecKillConstants.*;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private RedissonClient redissonClient;

	@Resource
	private RabbitTemplate rabbitTemplate;

	// 内存标记,可以提高部分的响应速度
	private Map<String, Boolean> flag = new ConcurrentHashMap<>();

	@Override
	public List<SeckillSkuRelationTo> queryCuurentSecKillProducts() {
		List<String> keys = getKeys();
		log.info("查询到的key{}", keys);
		if (CollectionUtils.isEmpty(keys)) {
			return null;
		}
		String key = findLatestSessionkey(keys);
		List<Long> skuIds = getSkuIds(key);
		log.info("skuIds{}", skuIds);
		return getData(skuIds);
	}

	private List<SeckillSkuRelationTo> getData(List<Long> skuIds) {
		BoundHashOperations<String, String, SeckillSkuRelationTo> ops = redisTemplate.boundHashOps(SEC_KILL_SKUS);
		Set<String> keys = ops.keys();
		List<String> list = skuIds.stream().map(e -> e + "").collect(Collectors.toList());
		List<String> ids = keys.stream().filter(e -> {
			String[] strings = e.split("_");
			return list.contains(strings[1]);
		}).collect(Collectors.toList());
		log.info("过滤后的ids{}", ids);
		return ops.multiGet(ids);
	}


	private List<Long> getSkuIds(String key) {
		ListOperations<String, Object> ops = redisTemplate.opsForList();
		List<Object> objs = ops.range(key, 0, -1);
		List<List<Long>> data = DataUtil.typeConvert(objs, new TypeReference<List<List<Long>>>() {
		});
		return data.stream()
				.flatMap(e -> e.stream())
				.collect(Collectors.toList());
	}

	private String findLatestSessionkey(List<String> keys) {
		List<String[]> list = keys.stream()
				.map(e -> {
					e = e.replace("seckill:sessions", "");
					return e.split("_");
				}).collect(Collectors.toList());
		String s = list.stream()
				.map(ele -> ele[0])
				.min(Comparator.comparing(Long::valueOf)).get();
		log.info("过滤出的结果{}", s);
		String[] strings = list.stream().filter(element -> element[0].equals(s)).findAny().get();
		String pattern = String.join("_", strings);
		String key = keys.stream().filter(el -> el.contains(pattern)).findAny().get();
		log.info("查找到的key{}", key);
		return key;
	}

	private List<String> getKeys() {
		HashSet<String> result = redisTemplate.execute(connection -> {
			RedisAsyncCommands conn = (RedisAsyncCommands) connection.getNativeConnection();
			ScanCursor cursor = ScanCursor.INITIAL;
			HashSet<String> set = new HashSet<>();
			try {
				while (!cursor.isFinished()) {
					ScanArgs args = ScanArgs.Builder.limit(100).match("seckill:sessions*");
					RedisFuture<KeyScanCursor<byte[]>> future = conn.scan(cursor, args);
					cursor = future.get();
					future.get().getKeys().stream().distinct().forEach(e -> {
						set.add(new String(e, StandardCharsets.UTF_8));
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return set;
		}, true);
		return Objects.isNull(result) ? null : new ArrayList<>(result);
	}

	@Override
	public SeckillSkuRelationTo isInSecKill(Long skuId) {
		List<SeckillSkuRelationTo> seckillSkuRelationTos = queryCuurentSecKillProducts();
		SeckillSkuRelationTo relationTo = null;
		List<SeckillSkuRelationTo> relations = seckillSkuRelationTos.stream().filter(ele -> ele.getSkuInfo().getSkuId().equals(skuId)).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(relations)) {
			//	 再次查询
			relationTo = getNotInSecKillInfo(skuId);
		}
		relationTo = relations.stream().min(Comparator.comparing(SeckillSkuRelationTo::getStart)).get();
		return relationTo;
	}

	private SeckillSkuRelationTo getNotInSecKillInfo(Long skuId) {
		BoundHashOperations<String, String, SeckillSkuRelationTo> ops = redisTemplate.boundHashOps(SEC_KILL_SKUS);
		Set<String> keys = ops.keys();
		keys = keys.stream()
				.filter(e -> {
					String[] strings = e.split("_");
					return strings[1].equals(String.valueOf(skuId));
				}).collect(Collectors.toSet());
		if (!CollectionUtils.isEmpty(keys)) {
			List<SeckillSkuRelationTo> relations = ops.multiGet(keys);
			SeckillSkuRelationTo relation = relations.stream().min(Comparator.comparing(SeckillSkuRelationTo::getStart)).get();
			relation.setToken(null);
			return relation;
		}
		return null;
	}

	@Override
	public String createOrder(SecKillVo secKillVo) {
		// skus中查询商品信息,验证秒杀活动id于商品限购数量
		BoundHashOperations<String, String, SeckillSkuRelationTo> hashOps = redisTemplate.boundHashOps(SEC_KILL_SKUS);
		String key = secKillVo.getPromotionSessionId() + "_" + secKillVo.getSkuId();
		// 这个map中没有存储信息就代表着当前的这个商品应该还有余量...
		if (!flag.getOrDefault(key, true)) {
			return null;
		}
		if (Boolean.FALSE.equals(hashOps.hasKey(key))) {
			return null;
		}
		SeckillSkuRelationTo relationTo = hashOps.get(key);
		Long start = relationTo.getStart();
		Long end = relationTo.getEnd();
		// 秒杀时间校验
		if (new Date().before(new Date(start)) || new Date().after(new Date(end))) {
			return null;
		}
		// 验证token
		if (relationTo.getSeckillLimit() < secKillVo.getCount() || !relationTo.getToken().equals(secKillVo.getToken())) {
			return null;
		}
		// 查询用户是否是重复下单
		MemberEntity member = PermissionInterceptor.THREAD_LOCAL.get();
		SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
		if (Boolean.TRUE.equals(opsForSet.isMember(SEC_KILL_ORDER_LIST + ":" + relationTo.getPromotionSessionId(), member.getId()))) {
			return null;
		}
		// 抢占信号量
		RSemaphore semaphore = redissonClient.getSemaphore(SEC_KILL_STOCK + relationTo.getToken());
		try {
			boolean res = semaphore.tryAcquire(secKillVo.getCount(), 100, TimeUnit.MILLISECONDS);
			if (!res) {
				flag.put(key, false);
				return null;
			}
		} catch (InterruptedException e) {
			log.error("出错了{}", e);
			e.printStackTrace();
			return null;
		}
		opsForSet.add(SEC_KILL_ORDER_LIST + ":" + relationTo.getPromotionSessionId(), member.getId());
		String orderSn = new Snowflake(0, 0).nextIdStr();
		// 发送秒杀下单的消息
		SecKillOrderTo order = new SecKillOrderTo();
		order.setOrderSn(orderSn);
		order.setCount(secKillVo.getCount());
		order.setPromotionSessionId(relationTo.getPromotionSessionId());
		order.setSkuId(secKillVo.getSkuId());
		order.setSecKillPrice(relationTo.getSeckillPrice());
		order.setMemberId(member.getId());
		rabbitTemplate.convertAndSend(ORDER_EVENT_EXCHANGE, SEC_KILL_QUEUE_ORDER_BINDING, order, new CorrelationData(CodeUtils.randomUUID()));
		return orderSn;
	}


}
