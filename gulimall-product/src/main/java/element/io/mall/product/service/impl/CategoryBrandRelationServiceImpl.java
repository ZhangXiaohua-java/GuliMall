package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.BrandDao;
import element.io.mall.product.dao.CategoryBrandRelationDao;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.BrandEntity;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryBrandRelationService;
import element.io.mall.product.vo.BrandVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {


	@Resource
	private BrandDao brandDao;

	@Resource
	private CategoryDao categoryDao;

	@Resource
	private CategoryBrandRelationService categoryBrandRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public List<CategoryBrandRelationEntity> findCategoryBrandRelation(Long brandId) {
		LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper
				.select(CategoryBrandRelationEntity::getBrandName, CategoryBrandRelationEntity::getCatelogName, CategoryBrandRelationEntity::getId)
				.eq(CategoryBrandRelationEntity::getBrandId, brandId);
		return this.baseMapper.selectList(queryWrapper);
	}

	@SuppressWarnings({"all"})
	@Override
	public boolean saveRelation(CategoryBrandRelationEntity categoryBrandRelation) {
		Long brandId = categoryBrandRelation.getBrandId();
		Long catelogId = categoryBrandRelation.getCatelogId();
		LambdaQueryWrapper<BrandEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.select(BrandEntity::getName)
				.eq(BrandEntity::getBrandId, brandId);
		String brandName = brandDao.selectOne(wrapper).getName();
		LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(CategoryEntity::getName)
				.eq(CategoryEntity::getCatId, catelogId);
		String catelogName = categoryDao.selectOne(queryWrapper).getName();
		if (StringUtils.hasText(brandName) && StringUtils.hasText(catelogName)) {
			categoryBrandRelation.setBrandName(brandName);
			categoryBrandRelation.setCatelogName(catelogName);
			this.baseMapper.insert(categoryBrandRelation);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<BrandVo> queryBrandRelations(Long catId) {
		LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.select(CategoryBrandRelationEntity::getBrandName, CategoryBrandRelationEntity::getBrandId);
		wrapper.eq(CategoryBrandRelationEntity::getCatelogId, catId);
		List<CategoryBrandRelationEntity> relationEntities = this.baseMapper.selectList(wrapper);
		List<BrandVo> vos = relationEntities.stream().map(e -> {
			BrandVo vo = new BrandVo();
			vo.setBrandId(e.getBrandId());
			vo.setBrandName(e.getBrandName());
			return vo;
		}).collect(Collectors.toList());
		return vos;
	}
	

}