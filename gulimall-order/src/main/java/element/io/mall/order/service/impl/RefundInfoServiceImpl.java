package element.io.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.dao.RefundInfoDao;
import element.io.mall.order.entity.RefundInfoEntity;
import element.io.mall.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("refundInfoService")
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoDao, RefundInfoEntity> implements RefundInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}