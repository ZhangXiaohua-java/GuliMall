package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.SpuInfoEntity;
import element.io.mall.product.vo.SpuInfoVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

	PageUtils queryPage(Map<String, Object> params);

	boolean saveSpuInfo(SpuInfoVo spuInfoVo);


	void saveBasicInfo(SpuInfoEntity spuInfoEntity);
	
}

