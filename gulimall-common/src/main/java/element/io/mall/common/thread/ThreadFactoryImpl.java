package element.io.mall.common.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 张晓华
 * @date 2022-11-19
 */
public class ThreadFactoryImpl implements java.util.concurrent.ThreadFactory {

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		int count = atomicInteger.getAndIncrement();
		thread.setName("th:" + count);
		thread.setPriority(Thread.NORM_PRIORITY);
		return thread;
	}


}
