package element.io.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.dao.OrderItemDao;
import element.io.mall.order.entity.OrderItemEntity;
import element.io.mall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public List<OrderItemEntity> batchQueryItemsByOrderSnCollection(List<String> orderSnCollection) {
		LambdaQueryWrapper<OrderItemEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(OrderItemEntity::getOrderSn, orderSnCollection);
		return this.list(wrapper);
	}
	

}