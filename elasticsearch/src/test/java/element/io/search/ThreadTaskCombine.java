package element.io.search;

import element.io.mall.common.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-11-20
 */
@Slf4j
public class ThreadTaskCombine {

	private static ThreadPoolExecutor threadPool;
	private static DateTimeFormatter dateTimeFormatter;

	static {
		threadPool = ThreadUtils.getThreadPool(1, 3, 30, 3);
		dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}

	public static void main(String[] args) throws Exception {
		log.info("main--->>>start");
		compose();
		log.info("main--->>>end");
	}

	/**
	 * 两个任务组合,在两个任务都结束之后才能够执行第三个任务
	 * runAfterBothXXX,不能获取到前两个任务的执行结果,也不能返回任何处理结果
	 * thenAcceptBothXXX 可以获取到前两个异步任务的执行结果,但是不能返回任何的处理结果,只能够消费
	 * thenCombineXXX 既可以获取前两个任务的执行结果,也可以返回自己的处理结果
	 */
	public static void taskCombine() throws ExecutionException, InterruptedException {

		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			log.info("task1的执行时间: {}", dateTimeFormatter.format(LocalDateTime.now()));
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return "abc";
		}, threadPool);

		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			log.info("task2的执行时间: {}", dateTimeFormatter.format(LocalDateTime.now()));
			return "def";
		}, threadPool);
		future01.runAfterBothAsync(future02, () -> {
			log.info("两个异步任务都已经执行完毕,当前的系统时间:{}", dateTimeFormatter.format(LocalDateTime.now()));
		}, threadPool);
		log.info("task1的返回值{},task2的返回值{}", future01.get(), future02.get());
	}

	public static void accept() {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			log.info("task1的执行时间: {}", dateTimeFormatter.format(LocalDateTime.now()));
			return "123";
		}, threadPool);
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			log.info("task2的执行时间: {}", dateTimeFormatter.format(LocalDateTime.now()));
			return "456";
		}, threadPool);
		CompletableFuture<Void> com = future01.thenAcceptBothAsync(future02, (r1, r2) -> {
			log.info("两个任务执行完毕之后的系统时间{}", dateTimeFormatter.format(LocalDateTime.now()));
			log.info("task1的执行结果{},task2的执行结果{}", r1, r2);
		}, threadPool);
	}

	public static void combine() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> "123", threadPool);
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> "456", threadPool);
		CompletableFuture<String> combineAsync = future01.thenCombineAsync(future02, (r1, r2) -> {
			log.info("任务3的执行时间: {}", dateTimeFormatter.format(LocalDateTime.now()));
			return r1 + r2;
		}, threadPool);
		String result = combineAsync.get();
		log.info("最终的执行结果是 {}", result);
	}


	public static void apply() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> "123");
		CompletableFuture<String> future = future01.thenApplyAsync((r) -> {
			return r + "后缀";
		}, threadPool);
		String result = future.get();
		log.info("获取到的最终结果{}", result);
	}

	/**
	 * 两个任务组合,只需要等待一个任务完成就可以
	 * runAfterEitherXXX 不能获取到任务的执行结果,即不能进行消费,也不能返回任何处理结果
	 * acceptEitherXXX 可以消费先完成的任务的结果,但是不能返回处理结果
	 * applyToEitherXXX 既可以消费先完成的任务的执行结果,还可以返回自己的处理结果
	 */
	public static void waitAfterOneTaskFinished() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			log.info("任务1开始执行");
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			log.info("任务1执行结束时的系统时间{}", dateTimeFormatter.format(LocalDateTime.now()));
			return "123";
		});
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			log.info("任务2开始执行");
			return "456";
		});
		future01.runAfterEither(future02, () -> {
			log.info("第三个任务开始执行,当前的系统时间{}", dateTimeFormatter.format(LocalDateTime.now()));

		});
		String s1 = future01.get();
		String s2 = future02.get();
		log.info("任务1的执行结果{},任务2的执行结果{}", s1, s2);

	}

	public static void waitAfterOneTaskFinished(int num) throws ExecutionException, InterruptedException {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			log.info("任务1开始执行,当前的系统时间是{}", dateTimeFormatter.format(LocalDateTime.now()));
			return "123";
		}, threadPool);
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			log.info("任务2开始执行");
			return "456";
		}, threadPool);
		future01.acceptEitherAsync(future02, (result) -> {
			log.info("获取到的先完成的任务的结果是 {}", result);
		}, threadPool);
		log.info("任务1的执行结果{},任务2的执行结果{}", future01.get(), future02.get());
	}

	public static void waitAfterOneTaskFinished(String str) throws ExecutionException, InterruptedException {
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			log.info("任务1开始执行");
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return "hello";
		}, threadPool);
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			log.info("任务2开始执行");
			return "JUC";
		}, threadPool);
		CompletableFuture<String> future03 = future02.applyToEitherAsync(future01, (result) -> {
			log.info("任务3什么也不做,直接返回结果");
			return result;
		}, threadPool);
		String result = future03.get();
		log.info("最终的执行结果{}", result);
	}

	public static void multiTasks() throws ExecutionException, InterruptedException {
		CompletableFuture<String> res = CompletableFuture.supplyAsync(() -> {
			log.info("查询到了sku的基本信息");
			return "sku信息 iPhone 15 pro max 紫色";
		}, threadPool).thenApplyAsync((skuInfo) -> {
			log.info("查询到了spu的基本信息");
			return skuInfo + "spu编号 112564654965";
		}, threadPool);
		CompletableFuture<String> stockFuture = CompletableFuture.supplyAsync(() -> {
			log.info("查询到了sku的库存信息");
			return "库存剩余10件";
		}, threadPool);
		CompletableFuture<Void> future = CompletableFuture.allOf(res, stockFuture);
		future.join();
		String result = res.get();
		String stock = stockFuture.get();
		log.info("获取到的最终结果: 商品信息{},库存信息{}", result, stock);

	}


	public static void multiTasks(int num) throws ExecutionException, InterruptedException {
		CompletableFuture<Void> helloWorld = CompletableFuture.runAsync(() -> {
			log.info("hello World");
			log.info("任务1执行完毕");
		});
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			log.info("任务2执行完毕");
			return "I'm Supplier interface implemention";
		});

		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			log.info("任务3执行完毕");
			return "...";
		});
		CompletableFuture<Object> any = CompletableFuture.anyOf(helloWorld, future, completableFuture);
		any.join();
		log.info("在三个任务之外的日志打印");
		//sempaphore.join();
		//log.info("任务1的结果{},任务2的结果{},任务3的结果{}", helloWorld.get(), future.get(), completableFuture.get());

	}

	/**
	 * thenComposeXXX 连接两个不同的CompletableFuture并返回一个新的CompletableFuture对象
	 * thenComposeXXX 可以用于组合多个CompletableFuture对象,可以将前一个任务的执行结果作为当前thenComposeXXX任务执行所需要的参数
	 * thenCompose组合的任务之间通常都是有先后顺序的,接收到前一个任务的结果,并且需要返回一个CompletableFuture对象,
	 * 这个任务中完成接收到前一个任务结果后需要执行的逻辑
	 */
	public static void compose() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			return "123";
		}, threadPool);
		CompletableFuture<String> res = future.thenComposeAsync(preResult -> CompletableFuture.supplyAsync(() -> {
			log.info("获取到的上一个任务的结果: {}", preResult);
			return preResult + "自定义后缀";
		}));
		String resultStr = res.get();
		log.info("获取到的最终结果: {}", resultStr);
	}


}
