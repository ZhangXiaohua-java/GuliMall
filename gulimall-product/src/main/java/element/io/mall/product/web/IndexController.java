package element.io.mall.product.web;

import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.CatelogLevel2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.redisson.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@Slf4j
@Controller
public class IndexController {

	@Resource
	private CategoryService categoryService;

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private RedissonClient redissonClient;

	@GetMapping("/")
	public String index(Model model) {
		List<CategoryEntity> categories = categoryService.findLevel1Categories();
		model.addAttribute("categories", categories);
		return "index";
	}


	@ResponseBody
	@GetMapping("/categories")
	public Map<String, List<CatelogLevel2Vo>> getCatelogies() throws IOException {
		Map<String, List<CatelogLevel2Vo>> categories = categoryService.findCategories();
		File file = new File("D:/data.json");
		FileUtils.write(file, categories.toString(), StandardCharsets.UTF_8);
		return categories;
	}


	@GetMapping(value = "/hello", produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String hello() {
		return "hello";
	}


	@GetMapping(value = "/read", produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String read() {
		ValueOperations ops = redisTemplate.opsForValue();
		RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("read&write_lock");
		RLock readLock = null;
		try {
			readLock = readWriteLock.readLock();
			readLock.lock();
			log.info("加读锁>>>");
			String content = Objects.nonNull(ops.get("content")) ? ops.get("content").toString() : "没有获取到数据";
			readLock.unlock();
			return content;
		} catch (Exception e) {
			return "锁的操作出问题了...";
		} finally {
			log.info("读锁释放成功---");
			readLock.unlock();
		}
	}


	@ResponseBody
	@GetMapping(value = "/lock", produces = {"application/json;charset=UTF-8"})
	public String getLockWithTimeLimit() throws InterruptedException {
		RLock lock = redissonClient.getLock("lock_with_time_limit");
		boolean hasLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
		if (hasLocked) {
			TimeUnit.SECONDS.sleep(30);
			return "成功获取到锁";
		} else {
			return "获取锁失败";
		}
	}

	@ResponseBody
	@GetMapping(value = "/write", produces = {"application/json;charset=UTF-8"})
	public String write() {
		RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("read&write_lock");
		RLock writeLock = readWriteLock.writeLock();
		ValueOperations ops = redisTemplate.opsForValue();
		try {
			writeLock.lock();
			log.info("加写锁---");
			String uuid = UUID.randomUUID().toString();
			ops.set("content", uuid);
			TimeUnit.SECONDS.sleep(30);
			return uuid;
		} catch (Exception e) {
			return "锁操作出问题了...";
		} finally {
			log.info("写锁释放成功>>>");
			writeLock.unlock();
		}
	}

	@ResponseBody
	@GetMapping(value = "/park", produces = "application/json;charset=UTF-8")
	public String park() throws InterruptedException {
		RSemaphore semaphore = redissonClient.getSemaphore("semaphore_usage");
		semaphore.acquire();
		return "停车成功";
	}

	@ResponseBody
	@GetMapping(value = "/leave", produces = "application/json;charset=UTF-8")
	public String leave() {
		RSemaphore semaphore = redissonClient.getSemaphore("semaphore_usage");
		semaphore.release();
		return "腾出一个停车位";
	}

	@ResponseBody
	@GetMapping(value = "/tyr/park", produces = "application/json;charset=UTF-8")
	public String tryPark() {
		RSemaphore semaphore = redissonClient.getSemaphore("semaphore_usage");
		if (semaphore.tryAcquire()) {
			return "停车成功";
		} else {
			return "再换一家停车场吧";
		}
	}

	@ResponseBody
	@GetMapping(value = "/close", produces = "application/json;charset=UTF-8")
	public String closeDoor() throws InterruptedException {
		RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("door");
		countDownLatch.trySetCount(5L);
		countDownLatch.await();
		return "放假了...";
	}

	@ResponseBody
	@GetMapping(value = "/go/{id}", produces = "application/json;charset=UTF-8")
	public String go(@PathVariable Integer id) {
		RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("door");
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
		if (list.contains(id)) {
			list = list.stream().filter(e -> !e.equals(id)).collect(Collectors.toList());
			countDownLatch.countDown();
			return id + "班级的大门关闭了";
		} else {
			return "无效操作";
		}
	}

	
}
