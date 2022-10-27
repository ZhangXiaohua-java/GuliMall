package element.io.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.dao.OrderDao;
import element.io.mall.order.entity.OrderEntity;
import element.io.mall.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}