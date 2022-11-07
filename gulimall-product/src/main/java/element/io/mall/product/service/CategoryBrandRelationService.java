package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<CategoryBrandRelationEntity> findCategoryBrandRelation(Long brandId);


	boolean saveRelation(CategoryBrandRelationEntity categoryBrandRelation);

	List<BrandVo> queryBrandRelations(Long catId);


}

