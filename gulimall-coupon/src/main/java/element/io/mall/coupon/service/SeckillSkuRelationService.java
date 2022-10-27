package element.io.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

