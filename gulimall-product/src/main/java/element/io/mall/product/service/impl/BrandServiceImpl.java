package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.BrandDao;
import element.io.mall.product.dao.CategoryBrandRelationDao;
import element.io.mall.product.entity.BrandEntity;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

	@Resource
	CategoryBrandRelationDao categoryBrandRelationDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Integer pageNum = Integer.valueOf(params.get("page").toString());
		Integer pageSize = Integer.valueOf(params.get("limit").toString());
		IPage<BrandEntity> page = new Page<>(pageNum, pageSize);
		this.baseMapper.selectPage(page, null);
		log.info("当前的数据总条目数是{}", page.getTotal());
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public boolean updateBrandInfoCaseCade(BrandEntity brand) {
		if (!StringUtils.hasText(brand.getName())) {
			return this.baseMapper.updateById(brand) == 1;
		}
		BrandEntity brandEntity = this.baseMapper.selectById(brand.getBrandId());
		if (brandEntity.getName().equals(brand.getName())) {
			return this.baseMapper.updateById(brand) == 1;
		}
		CategoryBrandRelationEntity relation = new CategoryBrandRelationEntity();
		relation.setBrandName(brand.getName());
		LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CategoryBrandRelationEntity::getBrandId, brand.getBrandId());
		categoryBrandRelationDao.update(relation, wrapper);
		return this.baseMapper.updateById(brand) == 1;
	}
}