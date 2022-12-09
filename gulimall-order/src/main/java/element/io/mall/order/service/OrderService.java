package element.io.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niezhiliang.simple.pay.dto.AlipayPcPayDTO;
import element.io.mall.common.to.OrderStatusTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.entity.OrderEntity;
import element.io.mall.order.vo.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 19:05:36
 */
public interface OrderService extends IService<OrderEntity> {

	PageUtils queryPage(Map<String, Object> params);

	CheckResponseVo checkToPay() throws ExecutionException, InterruptedException;


	SubmitOrderResponseVo createOrder(OrderRequestVo vo) throws IOException;


	OrderStatusTo queryOrderStatus(String orderSn);

	OrderEntity queryOrderByOrderSn(String orderSn);

	AlipayPcPayDTO getOrderInfoByOrderSn(String orderSn) throws UnsupportedEncodingException;

	List<OrderWithOrderItemVo> queryUserOrders(Integer pageNum, Integer pageSize);


	boolean updateOrderStatus(PayAsyncVo payAsyncVo);
	
}

