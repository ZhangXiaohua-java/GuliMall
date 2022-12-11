package element.io.secskill.service.impl;

import com.alibaba.fastjson.TypeReference;
import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.mall.common.util.DataUtil;
import element.io.secskill.service.SecKillService;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.SecKillConstants.SEC_KILL_SKUS;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Slf4j
@Service
public class SecKillServiceImpl implements SecKillService {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

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

}
