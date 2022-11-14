package element.io.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.to.SkuStockVo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
public interface WareSkuService extends IService<WareSkuEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void batchAddRecord(List<WareSkuEntity> wareSkuEntities);

	List<SkuStockVo> queryStock(Long[] skuIds);

}

