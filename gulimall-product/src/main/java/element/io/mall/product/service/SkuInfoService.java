package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.SkuInfoEntity;
import element.io.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<SkuInfoEntity> querySkusWithSpuId(Long spuId);


	SkuItemVo getSkuDetailInfo(Long skuId) throws ExecutionException, InterruptedException;

}

