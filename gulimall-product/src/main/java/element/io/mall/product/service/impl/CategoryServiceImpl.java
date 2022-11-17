package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.CategoryBrandRelationDao;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.CatelogLevel2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static element.io.mall.common.util.DataUtil.ifNull;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


	@Resource
	private CategoryBrandRelationDao relationDao;

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private RedissonClient redissonClient;

	private Comparator<CategoryEntity> categoryEntityComparator = (pre, curr) -> pre.getSort() == null ? 0 : pre.getSort() - (curr.getSort() == null ? 0 : curr.getSort());

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public List<CategoryEntity> listCategoriesWithTree() {

		List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
		log.info("查找的分类列表的长度{}", categoryEntities.size());
		List<CategoryEntity> level0 = categoryEntities.stream()
				.distinct()
				.filter(e -> e.getParentCid() == 0)
				.sorted(categoryEntityComparator)
				.map(category -> findChildren(category, categoryEntities))
				.collect(Collectors.toList());
		return level0;
	}

	public CategoryEntity findChildren(CategoryEntity category, List<CategoryEntity> all) {
		List<CategoryEntity> list = all.stream().distinct()
				.filter(e -> e.getParentCid() == category.getCatId())
				.sorted(categoryEntityComparator)
				.map(c -> findChildren(c, all)).collect(Collectors.toList());
		category.setChildren(list);
		return category;
	}

	@Override
	public Long[] findCategoryPath(Long categoryId) {
		ArrayList<Long> list = new ArrayList<>();
		this.findParent(categoryId, list);
		Collections.reverse(list);
		return list.toArray(new Long[]{});
	}

	@Override
	public void findParent(Long id, List<Long> path) {
		LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CategoryEntity::getCatId, id);
		CategoryEntity categoryEntity = this.baseMapper.selectOne(queryWrapper);
		if (Objects.nonNull(categoryEntity)) {
			path.add(categoryEntity.getCatId());
			findParent(categoryEntity.getParentCid(), path);
		} else {
			return;
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "category", key = "'findCategories'"),
			@CacheEvict(value = "category", key = "'level1Categories'")
	})
	@Override
	public boolean updateCategoryInfoCaseCade(CategoryEntity category) {
		if (!StringUtils.hasText(category.getName())) {
			int count = this.baseMapper.updateById(category);
			return count == 1;
		}
		CategoryEntity categoryEntity = this.baseMapper.selectById(category.getCatId());
		if (!categoryEntity.getName().equals(category.getName())) {
			LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId());
			CategoryBrandRelationEntity relation = new CategoryBrandRelationEntity();
			relation.setCatelogName(category.getName());
			relationDao.update(relation, wrapper);
			return this.baseMapper.updateById(category) == 1;
		} else {
			return this.baseMapper.updateById(category) == 1;
		}

	}

	// 改用Spring Cache来简化缓存的操作
	@Cacheable(value = "category", key = "#root.methodName")
	@Override
	public Map<String, List<CatelogLevel2Vo>> findCategories() throws IOException {
		return this.queryCategoriesFromDb();
	}

	/**
	 * @Override public Map<String, List<CatelogLevel2Vo>> findCategories() throws IOException {
	 * // 简单的分布式锁
	 * ValueOperations ops = redisTemplate.opsForValue();
	 * // 从Redis中查询数据
	 * Object data = ops.get(CATELOGRIES);
	 * if (Objects.isNull(data)) {
	 * // 如果查询到的数据为null,则从数据库中查询数据并存入到Redis中
	 * RLock lock = redissonClient.getLock("catalog_lock");
	 * lock.lock();
	 * // 保险起见可以再查询一次,查询到数据就可以直接返回
	 * data = ops.get(CATELOGRIES);
	 * if (Objects.isNull(data)) {
	 * Map<String, List<CatelogLevel2Vo>> map = queryCategoriesFromDb();
	 * ops.set(CATELOGRIES, map);
	 * lock.unlock();
	 * return map;
	 * } else {
	 * lock.unlock();
	 * return DataUtil.typeConvert(data, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * }
	 * return DataUtil.typeConvert(data, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 */

	public Map<String, List<CatelogLevel2Vo>> queryCategoriesFromDb() {
		log.info("执行了数据库查询操作");
		List<CategoryEntity> categoryEntities = this.baseMapper.selectAllCategories();
		categoryEntities.stream().sorted((i1, i2) -> ifNull(i1.getSort()) - ifNull(i2.getSort()));
		// 分别收集一级分类的信息,二级和三级的信息
		List<CategoryEntity> level1 = categoryEntities.stream().distinct()
				.filter(category -> category.getCatLevel().equals(Integer.valueOf(1))).collect(Collectors.toList());
		List<CategoryEntity> level2 = categoryEntities.stream().distinct().filter(c -> c.getCatLevel().equals(Integer.valueOf(2))).collect(Collectors.toList());
		List<CategoryEntity> level3 = categoryEntities.stream().distinct().filter(c -> c.getCatLevel().equals(Integer.valueOf(3))).collect(Collectors.toList());
		List<List<CatelogLevel2Vo>> collect = level1.stream().map(item -> {
			List<CatelogLevel2Vo> level2Vos = level2.stream()
					.filter(e -> e.getParentCid().equals(item.getCatId()))
					.map(l2 -> {
						CatelogLevel2Vo level2Vo = new CatelogLevel2Vo();
						level2Vo.setId(l2.getCatId().toString());
						level2Vo.setName(l2.getName());
						level2Vo.setCatalog1Id(item.getCatId().toString());
						List<CatelogLevel2Vo.CatelogLevel3Vo> level3Vos = level3.stream()
								.filter(e -> e.getParentCid().equals(l2.getCatId()))
								.map(l3 -> {
									CatelogLevel2Vo.CatelogLevel3Vo level3Vo = new CatelogLevel2Vo.CatelogLevel3Vo(level2Vo.getId(), l3.getCatId().toString(), l3.getName());
									return level3Vo;
								}).collect(Collectors.toList());
						level2Vo.setCatalog3List(level3Vos);
						return level2Vo;
					}).collect(Collectors.toList());
			return level2Vos;
		}).collect(Collectors.toList());
		HashMap<String, List<CatelogLevel2Vo>> map = new HashMap<>();
		for (int i = 0; i < level1.size(); i++) {
			map.put(level1.get(i).getCatId().toString(), collect.get(i));
		}
		return map;
	}

	// 想要用字符串常量就必须在使用一个单引号括起来,否则Spring Cache就会将其当成SpEL表达式解析
	@Cacheable(value = {"category"}, key = "'level1Categories'")
	@Override
	public List<CategoryEntity> findLevel1Categories() {
		log.info("从数据库查询了level1Categories的数据");
		LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(CategoryEntity::getName, CategoryEntity::getCatId);
		queryWrapper.eq(CategoryEntity::getParentCid, 0);
		return this.baseMapper.selectList(queryWrapper);
	}

	/**
	 * 本地锁,同步代码块
	 *
	 * @Override public Map<String, List<CatelogLevel2Vo>> findCategories() {
	 * // 简单的分布式锁
	 * ValueOperations ops = redisTemplate.opsForValue();
	 * // 从Redis中查询数据
	 * synchronized (Object.class) {
	 * Object data = ops.get(CATELOGRIES);
	 * if (Objects.isNull(data)) {
	 * Object dd = ops.get(CATELOGRIES);
	 * if (Objects.nonNull(dd)) {
	 * log.info("获取到了锁,但是从Reids中查到了数据,,放弃查询数据库");
	 * return DataUtil.typeConvert(dd, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * log.info("缓存未命中,加锁查询数据库");
	 * Map<String, List<CatelogLevel2Vo>> map = queryCategoriesFromDb();
	 * ops.set(CATELOGRIES, map, Duration.ofSeconds(60));
	 * return map;
	 * }
	 * }
	 * log.info("缓存命中,直接返回数据");
	 * return DataUtil.typeConvert(data, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * 利用Redis实现简单的分布式锁,最大的缺点在于解锁是一个非原子操作,仍有可能误删操作
	 * @Override public Map<String, List<CatelogLevel2Vo>> findCategories() {
	 * // 简单的分布式锁
	 * ValueOperations ops = redisTemplate.opsForValue();
	 * // 从Redis中查询数据
	 * Object data = ops.get(CATELOGRIES);
	 * if (Objects.isNull(data)) {
	 * String uuid = UUID.randomUUID().toString();
	 * // 上锁(自旋锁,但是会有栈内存溢出的风险)
	 * if (!ops.setIfAbsent("lock", uuid, Duration.ofSeconds(30))) {
	 * try {
	 * TimeUnit.MILLISECONDS.sleep(300);
	 * } catch (InterruptedException e) {
	 * throw new RuntimeException(e);
	 * }
	 * return findCategories();
	 * }
	 * Object dd = ops.get(CATELOGRIES);
	 * if (Objects.nonNull(dd)) {
	 * log.info("获取到了锁,但是从Reids中查到了数据,,放弃查询数据库");
	 * if (ops.get("lock").equals(uuid)) {
	 * // 不需要从数据库中查询数据之后也应当释放锁
	 * redisTemplate.delete("lock");
	 * }
	 * return DataUtil.typeConvert(dd, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * log.info("缓存未命中,加锁查询数据库");
	 * Map<String, List<CatelogLevel2Vo>> map = queryCategoriesFromDb();
	 * ops.set(CATELOGRIES, map, Duration.ofSeconds(60));
	 * // 将数据存入到Redis之后就需要及时地释放锁
	 * if (ops.get("lock").equals(uuid)) {
	 * redisTemplate.delete("lock");
	 * }
	 * return map;
	 * <p>
	 * }
	 * log.info("缓存命中,直接返回数据");
	 * return DataUtil.typeConvert(data, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * <p>
	 * 使用Lua脚本完成解锁的原子操作
	 * @Override public Map<String, List<CatelogLevel2Vo>> findCategories() throws IOException {
	 * // 简单的分布式锁
	 * ValueOperations ops = redisTemplate.opsForValue();
	 * // 从Redis中查询数据
	 * Object data = ops.get(CATELOGRIES);
	 * if (Objects.isNull(data)) {
	 * String uuid = UUID.randomUUID().toString().replace("-", "");
	 * // 上锁(自旋锁,但是会有栈内存溢出的风险)
	 * if (!ops.setIfAbsent("lock", uuid, Duration.ofSeconds(30))) {
	 * try {
	 * TimeUnit.MILLISECONDS.sleep(300);
	 * } catch (InterruptedException e) {
	 * throw new RuntimeException(e);
	 * }
	 * return findCategories();
	 * }
	 * Object dd = ops.get(CATELOGRIES);
	 * if (Objects.nonNull(dd)) {
	 * log.info("获取到了锁,但是从Reids中查到了数据,,放弃查询数据库");
	 * unlock("lock", uuid);
	 * return DataUtil.typeConvert(dd, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 * log.info("缓存未命中,加锁查询数据库");
	 * Map<String, List<CatelogLevel2Vo>> map = queryCategoriesFromDb();
	 * ops.set(CATELOGRIES, map, Duration.ofSeconds(60));
	 * unlock("lock", uuid);
	 * return map;
	 * <p>
	 * }
	 * log.info("缓存命中,直接返回数据");
	 * return DataUtil.typeConvert(data, new TypeReference<Map<String, List<CatelogLevel2Vo>>>() {
	 * });
	 * }
	 */

	// 解锁
	private void unlock(String lockName, String uuid) throws IOException {
		String script = new ResourceScriptSource(new ClassPathResource("delete.lua")).getScriptAsString();
		log.info("脚本内容{}", script);
		RedisScript deleteScript = RedisScript.of(script, Boolean.class);
		Boolean res = (Boolean) redisTemplate.execute(deleteScript, Arrays.asList(lockName), uuid);
		if (res) {
			log.info("脚本释放锁成功");
		} else {
			log.info("脚本释放锁失败");
		}
	}

}