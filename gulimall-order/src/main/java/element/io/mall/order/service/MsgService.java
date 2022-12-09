package element.io.mall.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.rabbitmq.client.Channel;
import element.io.mall.common.domain.MqMessageEntity;
import element.io.mall.common.enumerations.OrderStatusEnum;
import element.io.mall.common.msg.OrderTo;
import element.io.mall.common.util.CodeUtils;
import element.io.mall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import static element.io.mall.common.enumerations.MQConstants.*;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Service
@Slf4j
@RabbitListener(queues = {ORDER_RELEASE_QUEUE})
public class MsgService {

	@Resource
	private OrderService orderService;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@RabbitHandler
	public void autoCloseOrder(OrderTo order, Message message, Channel channel) throws IOException, AlipayApiException {
		long seq = message.getMessageProperties().getDeliveryTag();
		OrderEntity orderEntity = orderService.queryOrderByOrderSn(order.getOrderSn());
		if (Objects.isNull(orderEntity)) {
			channel.basicAck(seq, false);
			return;
		}
		if (orderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
			// 关闭订单
			OrderEntity entity = new OrderEntity();
			entity.setId(orderEntity.getId());
			entity.setStatus(OrderStatusEnum.CANCLED.getCode());
			// 取消订单前先收单,不让用户支付成功,支付相关的东西集成老是失败,
			// 导致订单关闭后用户仍然可以支付并且解锁库存,出现库存负数的情况
			// -------------------------------
			closePayOrder(order.getOrderSn());
			// ----------------------------
			orderService.updateById(entity);
			//	 向解锁库存的队列发送一条消息
			String uuid = CodeUtils.randomUUID();
			CorrelationData correlationData = new CorrelationData(uuid);
			MqMessageEntity msg = new MqMessageEntity();
			msg.setMessageId(uuid);
			msg.setCreateTime(new Date());
			msg.setRoutingKey(ORDER_DELAY_QUEUE_BINDING);
			msg.setToExchane(ORDER_EVENT_EXCHANGE);
			msg.setMessageStatus(0);
			msg.setClassType(MqMessageEntity.JSON);
			msg.setContent(JSON.toJSONString(order));
			rabbitTemplate.convertAndSend(ORDER_EVENT_EXCHANGE, ORDER_EVENT_STOCK_RELEASE_BINDING, order, correlationData);
			redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + uuid, msg, Duration.ofDays(1));
			channel.basicAck(seq, false);
		} else {
			channel.basicAck(seq, false);
		}
	}


	private void closePayOrder(String orderSn) throws AlipayApiException {
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", "2021000121697719", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCMOVP+aEiesZldsNTf5U6rlpsB+ky+bsarWnrnlx888UUoMiBZI2DE8ZcatqhNwZlAPn5/74KTAVm5xSQV9Aph+tYaCayz6foQLzkrukev1AgjCqoRW1kU0wYMmtxQb7yjXyKfPd8xowlek90RiqtLzTQyH2N3+YZWZRgBqwf4hbf5lqTO9hRvSLgB0z4AYuvm0ZMOQM1R9YYRPNzyBfhvU+x4wpw00Acr7uf3r96+Nq5TjGVx0IXaBWMXOoAOGLOdND/4ZB029/SxcIPHFHWAKbd24dFpPkcZZsdRqXcrQ5ahY6OInE9HtFYNFxqfh4rxooUC+15TzPkgUCppbpChAgMBAAECggEAPS82iyeNqFDuDod+G0O/E/ffIvnISr+jSFluj2bNZpYQCv30R3C8ZLLVr/5LacIz0Gh0YfZsDyDjub9fg4S8kwwUJ8BY7xgE2NMCKyZY6UunUULx+ijVsdRyK0XXI/HdaZu1S65/mPT+vZnfOCw9mhGr2MMBb/Qcvc3Wr8Ok8rcaK80s/mRbxf28jFarjcXRMurRbhPgtdQetNKt9NVi6fYO2af1q2GOxQ1/IS1gfWaLHcTmAub5GPzVa70SzMSXRzfDobPVfgLdEERTVWLigLCzQeQjV4Lw03nHvSUiFnyNS3nYaJoYwClqWflB8jtrTQ1O8wVmFH5dSbAFkMeL0QKBgQD649YqrwAyUIvabcxkWjjRdd4YxfBPpE5VWsCzdELDMCNG8M/+H6m4stM5RKBsTXGVl02RfYK/N1KIDIwKzZb1a8/hLVGP98rdoU5wngt4Ldz2D67t/fX1optSnIGsMxwzdMLyneX819ckL583JTW2PhmA32RUBuJZcYujnYSSLwKBgQCPFHf0z6DaVBR3yjaMjEMbeR2M5S7OKYJ7JteFtE5LD3vEXYZD0EJ4GIdAa0IjUCgIZ05Mqz6FbWWzd6mQs37gtvgOyEi8/+8Tqz8DI2SpfvYGq7ULNMvXp8InDA57jEXc7OUfnfNdVqu4QzSs6L42K3RtBW9/PIMMLyHHyHdmLwKBgCyeLi1DGA7aojSDSFEkdmxRwOC4+ua++qtVS2XerCzYN73a/7Ja4S0WqFfL65e3IhcPZF0WbBUjeor1aEZXj5wvwzVxgMrQr4RAtplykemahmxAF8T9YCuB8ot7h9ge8sn33t+U0xznKbymt3LwLCAv5Qs1Lzt+SezMM6AyWCxDAoGAG3gMh52M/h2CSHUz/8u13oJkH/aoA8CIYmhUnEC/fz2bv1lNO5uyByh+Xum4qtrqtKJQr/t4Z1lXWxswHK25QB8ghIYDIPXpylboJAwZeUj8ps80VNBwChkU9zYjWajWiNGaM3W5kUPgd+G/LSsJyZCSK5QNkdnmNuKdk6Ny+NUCgYEAsfOBG6hG1b4M7f/fOledvgb2ljbvh/yyBNtySjLYF5cldGFyDEtyJjsgmdlQXNVTChEbxzhcJuBU4WlV29zPKJJ8fDC7rFliRBc5ZxyiWXtBt1cef+RNoH7KI5NZYL2aatI3NrkbVsqIBLjBuDpVYF+RKt83S0LiJ/5a+KTxVxI=", "json", "utf-8", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmQCrB4aGcm28zFf3DV0cqaSKJzGm8i05F0klUhHqFXtJoZ2/JKZ1FtCcJbZMDr5jVCzbiEnEE47rTP+0WYDqqZz3DiOQILr/gyMWZFkjR2V0RdoeKvIi3W7bDTpEkkCsOSEZtZA3SRnEvdj1rfCGLPr74L23civyxnAawFLFm1ObVUWyatZ5rLswfmg2nK1rOSF5Cx+3LeJydXDkk7oGaum+SJe0k4Z34AijMUjLZMLBezDvNMeHfe/Cn2K+rChFCpOc3kv5Jfz7HES30hH4aweLiCFgd9EYFOVcZiz1MKqc2vt22XUEvbsohlOJHU9HCy4aj+qVDT91R3bzODBtMwIDAQAB", "RSA2");
		AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
		JSONObject bizContent = new JSONObject();
		bizContent.put("out_trade_no", orderSn);
		request.setBizContent(bizContent.toString());
		AlipayTradeCloseResponse response = alipayClient.execute(request);
		if (response.isSuccess()) {
			log.info("收单成功");
		} else {
			log.error("收单失败");
		}
	}


}
