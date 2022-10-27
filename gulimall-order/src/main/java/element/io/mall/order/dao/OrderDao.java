package element.io.mall.order.dao;

import element.io.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 19:05:36
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
