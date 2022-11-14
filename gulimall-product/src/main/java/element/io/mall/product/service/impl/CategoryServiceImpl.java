package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.CategoryBrandRelationDao;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.CatelogLevel2Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static element.io.mall.common.util.DataUtil.ifNull;


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


	@Override
	public Map<String, List<CatelogLevel2Vo>> findCategories() {
		// gc overhead limit,内存不够用了,call gc试试
		System.gc();
		List<CategoryEntity> categoryEntities = this.baseMapper.selectAllCategories();
		categoryEntities.stream().sorted((i1, i2) -> ifNull(i1.getSort()) - ifNull(i2.getSort()));
		// 分别收集一级分类的信息,二级和三级的信息
		List<CategoryEntity> level1 = categoryEntities.stream().distinct()
				.filter(category -> category.getCatLevel().equals(Integer.valueOf(1))).collect(Collectors.toList());
		List<CategoryEntity> level2 = categoryEntities.stream().distinct().filter(c -> c.getCatLevel().equals(Integer.valueOf(2))).collect(Collectors.toList());
		List<CategoryEntity> level3 = categoryEntities.stream().distinct().filter(c -> c.getCatLevel().equals(Integer.valueOf(3))).collect(Collectors.toList());
		List<List<CatelogLevel2Vo>> collect = level1.stream().map(item -> {
			List<CatelogLevel2Vo> level2Vos = level2.stream()
					.filter(e -> e.getParentCid().equals(item.getCatId()))
					.map(l2 -> {
						CatelogLevel2Vo level2Vo = new CatelogLevel2Vo();
						level2Vo.setId(l2.getCatId().toString());
						level2Vo.setName(l2.getName());
						level2Vo.setCatalog1Id(item.getCatId().toString());
						List<CatelogLevel2Vo.CatelogLevel3Vo> level3Vos = level3.stream()
								.filter(e -> e.getParentCid().equals(l2.getCatId()))
								.map(l3 -> {
									CatelogLevel2Vo.CatelogLevel3Vo level3Vo = new CatelogLevel2Vo.CatelogLevel3Vo(level2Vo.getId(), l3.getCatId().toString(), l3.getName());
									return level3Vo;
								}).collect(Collectors.toList());
						level2Vo.setCatalog3List(level3Vos);
						return level2Vo;
					}).collect(Collectors.toList());
			return level2Vos;
		}).collect(Collectors.toList());
		HashMap<String, List<CatelogLevel2Vo>> map = new HashMap<>();
		for (int i = 0; i < level1.size(); i++) {
			map.put(level1.get(i).getCatId().toString(), collect.get(i));
		}
		return map;
	}

	@Override
	public List<CategoryEntity> findLevel1Categories() {
		LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(CategoryEntity::getName, CategoryEntity::getCatId);
		queryWrapper.eq(CategoryEntity::getParentCid, 0);
		return this.baseMapper.selectList(queryWrapper);
	}


}