package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrGroupDao;
import element.io.mall.product.entity.AttrGroupEntity;
import element.io.mall.product.service.AttrGroupService;
import element.io.mall.product.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Resource
	private CategoryService categoryService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		int pageNum = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("limit").toString());
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Long catId = Long.valueOf(params.get("catId").toString());
		Page<AttrGroupEntity> page = new Page<>();
		LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<AttrGroupEntity>();
		// 如果catId的值为0,就是一个默认的请求
		if (catId.equals(Long.valueOf(0))) {
			page = this.baseMapper.selectPage(page, new QueryWrapper<AttrGroupEntity>());
			queryWrapper.eq(AttrGroupEntity::getCatelogId, key)
					.or().like(AttrGroupEntity::getAttrGroupName, key);

		} else {
			queryWrapper.eq(AttrGroupEntity::getCatelogId, catId)
					.or()
					.eq(StringUtils.hasText(key), AttrGroupEntity::getCatelogId, key)
					.or().like(StringUtils.hasText(key), AttrGroupEntity::getAttrGroupName, key);
		}
		page = this.baseMapper.selectPage(page, queryWrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public AttrGroupEntity findById(Long attrGroupId) {
		AttrGroupEntity attrGroupEntity = this.baseMapper.selectById(attrGroupId);
		Long[] categoryPath = categoryService.findCategoryPath(attrGroupEntity.getCatelogId());
		attrGroupEntity.setCategoryPath(categoryPath);
		return attrGroupEntity;
	}


}