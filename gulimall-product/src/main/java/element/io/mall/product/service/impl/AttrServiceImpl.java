package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.enumerations.AttrType;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrAttrgroupRelationDao;
import element.io.mall.product.dao.AttrDao;
import element.io.mall.product.dao.AttrGroupDao;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.entity.AttrGroupEntity;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.AttrService;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.AttrResponseVo;
import element.io.mall.product.vo.AttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Resource
	private AttrAttrgroupRelationDao attrgroupRelationDao;

	@Resource
	private AttrGroupDao attrGroupDao;

	@Resource
	private CategoryDao categoryDao;

	@Resource
	private CategoryService categoryService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	// 屎山,特此记录
	@Override
	public PageUtils queryForPage(Map<String, Object> params) {
		// 收集需要使用到的参数
		Long catlogId = Long.valueOf(params.get("catlogId").toString());
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageSize = Integer.parseInt(params.get("limit").toString());
		int pageNum = Integer.parseInt(params.get("page").toString());
		// sale 的值sale查询销售属性,为空则是查询基本属性
		String type = Objects.nonNull(params.get("type")) ? params.get("type").toString() : null;
		Page<AttrEntity> page = new Page<>(pageNum, pageSize);
		// 条件组装
		LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(StringUtils.hasText(key), AttrEntity::getAttrId, key)
				.or()
				.like(StringUtils.hasText(key), AttrEntity::getAttrName, key);
		if (Objects.nonNull(type)) {
			queryWrapper.eq(AttrEntity::getAttrType, AttrType.SALE_ATTR.getCode());
			//this.baseMapper.selectPage(page, queryWrapper);
		} else {
			queryWrapper.eq(AttrEntity::getAttrType, AttrType.BASE_ATTR.getCode());
		}
		// 查询出原始的AttrEntity的数据集
		this.baseMapper.selectPage(page, queryWrapper);
		List<AttrEntity> records = page.getRecords();
		ArrayList<Long> attrIds = new ArrayList<>();
		// 收集id
		records.stream().forEach(e -> attrIds.add(e.getAttrId()));
		// 从关联表中批量查找attr_group_id
		List<AttrAttrgroupRelationEntity> groupIds = attrgroupRelationDao.batchFindGroupId(attrIds);
		log.info("遍历到的groupIds的长度{}", groupIds.size());
		// 获取分组名和分组对应的分类id
		List<Long> groupId = groupIds.stream().map(e -> e.getAttrGroupId()).collect(Collectors.toList());
		List<AttrGroupEntity> names = CollectionUtils.isEmpty(groupId) ? new ArrayList<AttrGroupEntity>() : attrGroupDao.batchFindNames(groupId);
		List<Long> catelogIds = records.stream().map(e -> e.getCatelogId()).collect(Collectors.toList());
		// 查找分类名
		List<CategoryEntity> categoryEntities = categoryDao.batchFindCategoryNames(catelogIds);
		ArrayList<AttrResponseVo> vos = new ArrayList<>();
		log.info("records的长度{},namesIds的长度{},catelogNames的长度{}",
				records.size(), names.size(), categoryEntities.size());
		// 代码放这我看谁能维护
		for (int i = 0; i < records.size(); i++) {
			log.info("执行拷贝操作");
			AttrResponseVo vo = new AttrResponseVo();
			AttrEntity attrEntity = records.get(i);
			BeanUtils.copyProperties(attrEntity, vo);
			groupIds.stream().filter(ele -> ele.getAttrId().equals(attrEntity.getAttrId()))
					.findFirst()
					.ifPresent(c -> {
						vo.setAttrGroupId(c.getAttrGroupId());
					});
			if (Objects.nonNull(vo.getAttrGroupId()) && vo.getAttrGroupId() != 0) {
				names.stream()
						.filter(e -> e.getAttrGroupId().equals(vo.getAttrGroupId()))
						.findFirst().ifPresent(e -> vo.setGroupName(e.getAttrGroupName()));
			}
			categoryEntities.stream()
					.filter(item -> item.getCatId().equals(attrEntity.getCatelogId()))
					.findFirst()
					.ifPresent(e -> vo.setCatelogName(e.getName()));
			vos.add(vo);
		}
		return new PageUtils(vos, Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public boolean saveAttr(AttrVo attrVo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attrVo, attrEntity);
		int count = 0;
		if (attrVo.getAttrType().equals(AttrType.SALE_ATTR.getCode())) {
			count = this.baseMapper.insert(attrEntity);
			return count == 1;
		}
		count = this.baseMapper.insert(attrEntity);
		AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
		relationEntity.setAttrId(attrEntity.getAttrId());
		relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
		int num = attrgroupRelationDao.insert(relationEntity);
		return count == 1 && num == 1;
	}


	@Override
	public AttrResponseVo getDetail(Long attrId) {
		AttrResponseVo vo = new AttrResponseVo();
		AttrEntity attrEntity = this.baseMapper.selectById(attrId);
		BeanUtils.copyProperties(attrEntity, vo);
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
		AttrAttrgroupRelationEntity attrgroupRelation = attrgroupRelationDao.selectOne(wrapper);
		vo.setCategoryPath(categoryService.findCategoryPath(attrEntity.getCatelogId()));
		if (attrEntity.getAttrType().equals(AttrType.BASE_ATTR.getCode())) {
			vo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
		}
		return vo;
	}


	@Override
	public boolean updateInfo(AttrVo vo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(vo, attrEntity);
		// 更新基本信息
		int count = this.baseMapper.updateById(attrEntity);
		// 从数据库再查询一次数据验证当前修改的数据是否是销售属性,如果是就可以结束,如果不是就需要接着修改关联表
		if (this.baseMapper.selectById(vo.getAttrId()).getAttrType().equals(AttrType.SALE_ATTR.getCode())) {
			return count == 1;
		}
		AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
		relationEntity.setAttrId(vo.getAttrId());
		relationEntity.setAttrGroupId(vo.getAttrGroupId());
		LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
		AttrAttrgroupRelationEntity one = attrgroupRelationDao.selectOne(queryWrapper);
		if (Objects.nonNull(one)) {
			LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
			attrgroupRelationDao.update(relationEntity, wrapper);
		} else {
			attrgroupRelationDao.insert(relationEntity);
		}
		return count == 1;
	}

	@Override
	public List<AttrEntity> batchQueryAttrs(List<Long> attrIds) {
		LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(AttrEntity::getAttrId, attrIds);
		return this.baseMapper.selectList(wrapper);
	}

	@Override
	public List<Long> findQuickShowAttrs(List<Long> skuAttrIds) {
		LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.select(AttrEntity::getAttrId, AttrEntity::getSearchType)
				.eq(AttrEntity::getSearchType, 1)
				.and(!CollectionUtils.isEmpty(skuAttrIds), condition -> condition.in(AttrEntity::getAttrId, skuAttrIds));
		List<AttrEntity> entityList = this.baseMapper.selectList(queryWrapper);
		return entityList.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
	}

}