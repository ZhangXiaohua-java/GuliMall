package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrAttrgroupRelationDao;
import element.io.mall.product.dao.AttrDao;
import element.io.mall.product.dao.AttrGroupDao;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.entity.AttrGroupEntity;
import element.io.mall.product.service.AttrAttrgroupRelationService;
import element.io.mall.product.service.AttrGroupService;
import element.io.mall.product.service.AttrService;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.AttrGroupRelationVo;
import element.io.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Resource
	private CategoryService categoryService;

	@Resource
	private AttrAttrgroupRelationDao attrgroupRelationDao;

	@Resource
	private AttrDao attrDao;

	@Lazy
	@Resource
	private AttrAttrgroupRelationService attrAttrgroupRelationService;


	@Lazy
	@Resource
	private AttrService attrService;


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

	@Override
	public List<AttrEntity> queryRelationsByAttrGroupId(Map<String, Object> params) {
		Long attrGroupId = Objects.nonNull(params.get("attrGroupId")) ?
				Long.valueOf(params.get("attrGroupId").toString()) : null;
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupId);
		List<AttrAttrgroupRelationEntity> list = attrgroupRelationDao.selectList(wrapper);
		List<Long> attrIds = list.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
		List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
		return attrEntities;
	}


	@Override
	public PageUtils queryNoRelationAttrs(Map<String, Object> params) {
		// 收集参数
		Long attrGroupId = Objects.nonNull(params.get("attrGroupId")) ? Long.valueOf(params.get("attrGroupId").toString()) : null;
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 0;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 0;
		/** 查询所有的属性信息的要求
		 * 1 不能是当前的分组已经关联过的属性信息
		 * 2 不能是被其它分组关联的属性
		 * 3 没有关联分组的属性不允许使用
		 * 4  必须是当前分组所属分类下的所有属性,否则不允许使用
		 */
		LambdaQueryWrapper<AttrGroupEntity> groupWrapper = new LambdaQueryWrapper<AttrGroupEntity>()
				.select(AttrGroupEntity::getCatelogId)
				.eq(AttrGroupEntity::getAttrGroupId, attrGroupId);
		// 获取当前分组所属的分类ID
		Long catelogId = this.baseMapper.selectOne(groupWrapper).getCatelogId();
		// 查找这个分类下的所有的分组信息AttrGroupEntity
		LambdaQueryWrapper<AttrGroupEntity> relationEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		relationEntityLambdaQueryWrapper
				.select(AttrGroupEntity::getAttrGroupId)
				.eq(AttrGroupEntity::getCatelogId, catelogId);
		List<AttrGroupEntity> groupEntities = this.baseMapper.selectList(relationEntityLambdaQueryWrapper);
		// 收集分类关联的所有属性分组的group_id的集合
		List<Long> groupIds = groupEntities.stream().map(group -> group.getAttrGroupId()).collect(Collectors.toList());
		// 构造查询已经被关联的属性的id attr_id的条件
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		attrAttrgroupRelationEntityLambdaQueryWrapper.select(AttrAttrgroupRelationEntity::getAttrId)
				.in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds);
		// 已经关联过的group分组信息
		List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(attrAttrgroupRelationEntityLambdaQueryWrapper);
		// 收集所有已经被关联过的属性id集合
		List<Long> attrIds = relationEntities.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());
		Page<AttrEntity> page = new Page<>(pageNum, pageSize);
		// 查询该分类下的所有属性信息
		LambdaQueryWrapper<AttrEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		attrEntityLambdaQueryWrapper.eq(AttrEntity::getCatelogId, catelogId)
				.and(Objects.nonNull(attrIds) && !CollectionUtils.isEmpty(attrIds),
						condition -> condition.notIn(AttrEntity::getAttrId, attrIds))
				.or(StringUtils.hasText(key))
				.like(AttrEntity::getAttrName, key)
				.eq(StringUtils.hasText(key), AttrEntity::getAttrId, key);
		attrDao.selectPage(page, attrEntityLambdaQueryWrapper);
		// 获取到的属性信息
		List<AttrEntity> records = page.getRecords();
		return new PageUtils(records, Long.valueOf(page.getTotal()).intValue(), pageNum, pageSize);
	}


	@Override
	public boolean saveRelations(AttrGroupRelationVo[] relationVos) {
		List<AttrAttrgroupRelationEntity> entityList = Arrays.stream(relationVos).map(e -> {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrId(e.getAttrId());
			relationEntity.setAttrGroupId(e.getAttrGroupId());
			return relationEntity;
		}).collect(Collectors.toList());
		boolean saveBatch = attrAttrgroupRelationService.saveBatch(entityList);
		return saveBatch;
	}


	@Override
	public boolean batchDeleteRelations(AttrGroupRelationVo[] relationVos) {
		List<AttrAttrgroupRelationEntity> relationEntities = Arrays.stream(relationVos)
				.map(item -> {
					AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
					relationEntity.setAttrId(item.getAttrId());
					relationEntity.setAttrGroupId(item.getAttrGroupId());
					return relationEntity;
				}).collect(Collectors.toList());
		return attrAttrgroupRelationService.batchRemoveRelations(relationEntities);
	}


	@Override
	public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId) {
		LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
		// 指定分类下的所有分组信息
		List<AttrGroupEntity> attrGroupEntities = this.baseMapper.selectList(queryWrapper);
		// 分组id的收集
		List<Long> groupIds = attrGroupEntities.stream().map(group -> group.getAttrGroupId()).collect(Collectors.toList());
		// 收集属性的id
		List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationService.getAttrIds(groupIds);
		// 收集属性id
		List<Long> attrIds = attrgroupRelationEntities.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
		List<AttrEntity> attrEntities = attrService.batchQueryAttrs(attrIds);
		return attrGroupEntities.stream().map(item -> {
			AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
			BeanUtils.copyProperties(item, vo);
			Long groupId = item.getAttrGroupId();
			// 获取该分组对应的所有属性的id
			List<Long> ids = attrgroupRelationEntities.stream().filter(relation -> relation.getAttrGroupId().equals(groupId)).collect(Collectors.toList())
					.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());
			// 过滤出需要的属性信息
			List<AttrEntity> attrs = attrEntities.stream().filter(attr -> ids.contains(attr.getAttrId()))
					.collect(Collectors.toList());
			vo.setAttrs(attrs);
			return vo;

		}).collect(Collectors.toList());
	}


}