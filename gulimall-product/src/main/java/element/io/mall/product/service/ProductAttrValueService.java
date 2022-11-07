package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void batchSaveAttrs(List<ProductAttrValueEntity> productAttrValueEntities);
	

}

