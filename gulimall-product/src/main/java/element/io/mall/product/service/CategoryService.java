package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.vo.CatelogLevel2Vo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface CategoryService extends IService<CategoryEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<CategoryEntity> listCategoriesWithTree();

	Long[] findCategoryPath(Long categoryId);

	void findParent(Long id, List<Long> path);


	boolean updateCategoryInfoCaseCade(CategoryEntity category);


	Map<String, List<CatelogLevel2Vo>> findCategories() throws IOException;


	List<CategoryEntity> findLevel1Categories();


}

