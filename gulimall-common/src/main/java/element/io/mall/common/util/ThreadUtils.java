package element.io.mall.common.util;

import element.io.mall.common.thread.ThreadFactoryImpl;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-11-19
 */
public final class ThreadUtils {


	/**
	 * 创建自定义的线程池
	 *
	 * @param core     核心线程数
	 * @param max      最大线程数
	 * @param timeout  非核心线程的最大生存时间
	 * @param capacity 任务队列的容量
	 * @return 线程池
	 */
	public static ThreadPoolExecutor getThreadPool(int core, int max, long timeout, int capacity) {
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(core, max, timeout,
				TimeUnit.SECONDS, new LinkedBlockingDeque<>(capacity),
				new ThreadFactoryImpl(), new ThreadPoolExecutor.AbortPolicy());
		return poolExecutor;
	}


}
