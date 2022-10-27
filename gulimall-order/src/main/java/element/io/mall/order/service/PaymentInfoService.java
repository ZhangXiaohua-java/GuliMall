package element.io.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 19:05:36
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

