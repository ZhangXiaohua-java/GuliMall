package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

	PageUtils queryPage(Map<String, Object> params);

	boolean batchRemoveRelations(List<AttrAttrgroupRelationEntity> relationEntities);
	

}

