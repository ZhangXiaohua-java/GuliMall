package element.io.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.dao.PaymentInfoDao;
import element.io.mall.order.entity.PaymentInfoEntity;
import element.io.mall.order.service.PaymentInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoDao, PaymentInfoEntity> implements PaymentInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}