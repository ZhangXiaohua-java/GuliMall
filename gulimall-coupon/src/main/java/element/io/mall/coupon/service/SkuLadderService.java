package element.io.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

