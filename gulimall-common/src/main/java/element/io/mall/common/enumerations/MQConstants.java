package element.io.mall.common.enumerations;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
public final class MQConstants {

	public static final String REDIS_KEY_PREFIX = "mq:bak:";
	public static final String STOCK_EVENT_EXCHANGE = "stock.event.exchange";

	public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
	public static final String STOCK_RELEASE_QUEUE = "stock.release.queue";
	public static final String DELAY_QUEUE_ROUTING_KEY = "stock.delay";

	public static final String RELEASE_QUEUE_ROUTING_KEY = "stock.release";

	public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";

	public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

	public static final String ORDER_RELEASE_QUEUE = "order.release.queue";

	public static final String ORDER_RELEASE_QUEUE_BINDING = "order";

	public static final String ORDER_DELAY_QUEUE_BINDING = "order.delay";

	public static final String ORDER_EVENT_STOCK_RELEASE_BINDING = "release";

	public static final String SUB_STOCK_QUEUE = "stock.sub.queue";

	public static final String SUB_STOCK_QUEUE_BINDING = "stock.sub";


	public static final String SEC_KILL_QUEUE = "sec.kill.queue";

	public static final String SEC_KILL_QUEUE_ORDER_BINDING = "sec.kill.order";

	
}
