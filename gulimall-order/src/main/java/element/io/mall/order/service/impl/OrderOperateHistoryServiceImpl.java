package element.io.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.dao.OrderOperateHistoryDao;
import element.io.mall.order.entity.OrderOperateHistoryEntity;
import element.io.mall.order.service.OrderOperateHistoryService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderOperateHistoryService")
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryDao, OrderOperateHistoryEntity> implements OrderOperateHistoryService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}