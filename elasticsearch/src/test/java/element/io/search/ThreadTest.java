package element.io.search;

import element.io.mall.common.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author 张晓华
 * @date 2022-11-19
 */
@Slf4j
@SuppressWarnings({"all"})
public class ThreadTest {


	private static ThreadPoolExecutor threadPool;

	private static void initialize() {
		threadPool = ThreadUtils.getThreadPool(1, 5, 10, 10);
	}

	public static void main(String[] args) throws Exception {
		initialize();
		combine();
		TimeUnit.SECONDS.sleep(3);
		threadPool.shutdown();
	}

	/**
	 * 可以把 supply,apply,accept,run可以串成一个链
	 * supply异步执行,apply对supply的计算结果进行加工处理,accept消费apply计算的结果,
	 * run进行收尾操作
	 */
	public static void combine() {
		CompletableFuture.supplyAsync(() -> {
			log.info("开始计算");
			return "组合计算";
		}, threadPool).thenApplyAsync((result) -> {
			log.info("接收到前一个任务的执行结果: {},进行数据加工", result);
			return "处理后的结果" + result;
		}, threadPool).thenAcceptAsync((res) -> {
			log.info("接收到的结果{}", res);
		}, threadPool).thenRunAsync(() -> {
			log.info("组合任务结束");
		}, threadPool);
	}


	public static void serilalRunWithResultAndReturnValue() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			return "hello JUC";
		}, threadPool).thenApplyAsync((result) -> {
			log.info("获取到的前一个任务的执行结果{}", result);
			return "<" + result + ">";
		});
		String result = future.get();
		log.info("获取到的最终结果{}", result);
	}

	public static void serialRunWithResult() {
		CompletableFuture.supplyAsync(() -> {
			return "hello World";
		}, threadPool).thenAccept((result) -> {
			log.info("获取到执行结果{}", result);
		});
	}

	/**
	 * 线程串行化:
	 * thenRun(Runnable runnable) 无法获取到前一个异步任务的执行结果,也无法返回任何处理结果
	 * thenAccept(Consumer consumer) 可以获取到前一个异步任务的处理结果,但是也不能返回任何的处理结果
	 * thenApply(Function function)  不仅可以获取到前一个异步任务的执行结果,还可以返回处理结果
	 */
	public static void serialRunWithNoResult() {
		CompletableFuture.supplyAsync(() -> {
			return "线程串行化";
		}).thenRunAsync(() -> {
			log.info("thenRun方法无法获取到前一个异步任务的执行结果,也无法返回任何处理信息");
		});
	}


	/**
	 * handle可以对获取到正常执行的处理结果,也可以获取到执行中的错误信息,并且返回最终处理的结果
	 */
	public static void handle() throws ExecutionException, InterruptedException {
		Object result = CompletableFuture.supplyAsync(() -> {
			log.info("生产者开始执行...");
			if (1 == 1) {
				throw new RuntimeException("自定义异常");
			}
			return "生产者接口返回的结果";
		}, threadPool).handleAsync((res, error) -> {
			if (Objects.nonNull(error)) {
				log.error("执行出错了");
				return error.getMessage();
			} else {
				log.info("调用成功了");
				return res;
			}
		}, threadPool).get();
		log.info("获取到的最终结果: {}", result);
	}


	/**
	 * exceptionally(Function function) 可以对执行过程中的异常进行处理并返回一个默认的处理结果
	 * whenCompletionAsync 虽然可以获取到异常信息,但是并不能对异常进行处理
	 */
	public static void processErrorIfHasException() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			return "hello World";
		});
		CompletableFuture<String> error = future.whenComplete((result, throwable) -> {
			//throw new RuntimeException("自定义异常");
			log.info("任务二");
		}).exceptionally((eror) -> {
			log.error("执行出错了---");
			return "error <>";
		});
		String errorInfo = error.get();
		log.error("获取到的异常信息{}", errorInfo);
	}

	/**
	 * whenCompleteXXX 当前一个异步任务执行完毕之后才会执行当前传入的任务
	 * 如果不指定线程池则使用前一个任务的线程
	 */
	public static void callAfterCompletion() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			log.info("执行第一个任务");
			try {
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			int a = 10 / 0;
			return "hello JUC";
		}, threadPool);
		future.whenCompleteAsync((result, error) -> {
			if (Objects.nonNull(error)) {
				log.error("执行出错了...");
			} else {
				log.info("获取到前一个异步任务的执行结果是{}", result);
			}
		}, threadPool);
	}

	/**
	 * supplyAsync(Suppiler suppiler) 传入一个生产者接口,可以获取到计算结果
	 */
	public static void completableFutureCall() throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			log.info("hello JUC");
			return "hello 异步任务编排";
		});
		String result = future.get();
		log.info("获取到的执行结果{}", result);
	}

	/**
	 * 通过CompletableFuture类的runAsync方法传入一个Runnable接口的实现对象可以完成异步任务
	 */
	public static void completionFutureTest() {
		CompletableFuture.runAsync(() -> {
			log.info("hello JUC");
		}, threadPool);
	}


	public static void createThreadByExtend() {
		Thread thread = new SubClass();
		thread.start();
	}

	/**
	 * 通过Runnable接口创建的线程实例不能有返回值
	 */
	public static void createThreadByImplement() {
		new Thread(() -> {
			log.info("使用Runnable接口创建线程");
		}).start();
	}

	/**
	 * 通过Callable接口创建的线程实例可以返回计算结果
	 */
	public static void createThreadByImplement(int num) throws ExecutionException, InterruptedException {
		FutureTask<Integer> task = new FutureTask<>(() -> {
			log.info("callable实现类创建的线程");
			return 1 + 1;
		});
		new Thread(task).start();
		Integer result = task.get();
		log.info("获取到的结果{}", result);
	}

	/**
	 * 继承Thread类
	 */
	public static class SubClass extends Thread {
		@Override
		public void run() {
			log.info("hello");
		}
	}

	public static void anyTest() throws Exception {
		log.info("start");
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			log.info("我喜欢多线程操作");
		}).start();
		TimeUnit.SECONDS.sleep(10);
		log.info("end");
	}

}
