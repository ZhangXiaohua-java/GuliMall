package element.io.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.to.SkuReductionTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

	PageUtils queryPage(Map<String, Object> params);

	boolean saveReduction(SkuReductionTo skuReductionTo);


	void saveBasicReductionInfo(SkuFullReductionEntity skuFullReductionEntity);
	
}

