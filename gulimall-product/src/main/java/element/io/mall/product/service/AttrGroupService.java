package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.entity.AttrGroupEntity;
import element.io.mall.product.vo.AttrGroupRelationVo;
import element.io.mall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

	PageUtils queryPage(Map<String, Object> params);

	AttrGroupEntity findById(Long attrGroupId);


	List<AttrEntity> queryRelationsByAttrGroupId(Map<String, Object> params);

	PageUtils queryNoRelationAttrs(Map<String, Object> params);


	boolean saveRelations(AttrGroupRelationVo[] relationVos);


	boolean batchDeleteRelations(AttrGroupRelationVo[] relationVos);


	List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId);
	

}

