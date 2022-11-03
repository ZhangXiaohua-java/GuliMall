package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.CategoryBrandRelationDao;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


	@Resource
	private CategoryBrandRelationDao relationDao;

	private Comparator<CategoryEntity> categoryEntityComparator = (pre, curr) -> pre.getSort() == null ? 0 : pre.getSort() - (curr.getSort() == null ? 0 : curr.getSort());

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public List<CategoryEntity> listCategoriesWithTree() {

		List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
		log.info("查找的分类列表的长度{}", categoryEntities.size());
		List<CategoryEntity> level0 = categoryEntities.stream()
				.distinct()
				.filter(e -> e.getParentCid() == 0)
				.sorted(categoryEntityComparator)
				.map(category -> findChildren(category, categoryEntities))
				.collect(Collectors.toList());
		return level0;
	}

	public CategoryEntity findChildren(CategoryEntity category, List<CategoryEntity> all) {
		List<CategoryEntity> list = all.stream().distinct()
				.filter(e -> e.getParentCid() == category.getCatId())
				.sorted(categoryEntityComparator)
				.map(c -> findChildren(c, all)).collect(Collectors.toList());
		category.setChildren(list);
		return category;
	}

	@Override
	public Long[] findCategoryPath(Long categoryId) {
		ArrayList<Long> list = new ArrayList<>();
		this.findParent(categoryId, list);
		Collections.reverse(list);
		return list.toArray(new Long[]{});
	}

	@Override
	public void findParent(Long id, List<Long> path) {
		LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CategoryEntity::getCatId, id);
		CategoryEntity categoryEntity = this.baseMapper.selectOne(queryWrapper);
		if (Objects.nonNull(categoryEntity)) {
			path.add(categoryEntity.getCatId());
			findParent(categoryEntity.getParentCid(), path);
		} else {
			return;
		}
	}

	@Override
	public boolean updateCategoryInfoCaseCade(CategoryEntity category) {
		if (!StringUtils.hasText(category.getName())) {
			int count = this.baseMapper.updateById(category);
			return count == 1;
		}
		CategoryEntity categoryEntity = this.baseMapper.selectById(category.getCatId());
		if (!categoryEntity.getName().equals(category.getName())) {
			LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(CategoryBrandRelationEntity::getCatelogId, category.getCatId());
			CategoryBrandRelationEntity relation = new CategoryBrandRelationEntity();
			relation.setCatelogName(category.getName());
			relationDao.update(relation, wrapper);
			return this.baseMapper.updateById(category) == 1;
		} else {
			return this.baseMapper.updateById(category) == 1;
		}

	}


}