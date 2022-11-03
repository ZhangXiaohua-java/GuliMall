package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

	@Override
	public PageUtils queryForPage(Map<String, Object> params) {
		Long catlogId = Long.valueOf(params.get("catlogId").toString());
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageSize = Integer.parseInt(params.get("limit").toString());
		int pageNum = Integer.parseInt(params.get("page").toString());
		Page<AttrEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(StringUtils.hasText(key), AttrEntity::getAttrId, key)
				.or()
				.like(StringUtils.hasText(key), AttrEntity::getAttrName, key);
		// 查询出原始的AttrEntity的数据集
		this.baseMapper.selectPage(page, queryWrapper);
		List<AttrEntity> records = page.getRecords();
		ArrayList<Long> attrIds = new ArrayList<>();
		// 收集id
		records.stream().forEach(e -> attrIds.add(e.getAttrId()));
		// 从关联表中批量查找attr_group_id
		List<Long> groupIds = attrgroupRelationDao.batchFindGroupId(attrIds);
		log.info("遍历到的groupIds的长度{}", groupIds.size());
		// 获取分组名和分组对应的分类id
		List<AttrGroupEntity> names = attrGroupDao.batchFindNames(groupIds);
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
			BeanUtils.copyProperties(records.get(i), vo);
			AttrEntity attrEntity = records.get(i);
			String groupName = names.stream()
					.filter(e -> {
						if (e.getCatelogId().equals(attrEntity.getCatelogId())) {
							return true;
						} else {
							return false;
						}
					}).collect(Collectors.toList()).get(0).getAttrGroupName();
			vo.setGroupName(groupName);
			String catlogName = categoryEntities.stream()
					.filter(item -> {
						if (item.getCatId().equals(attrEntity.getCatelogId())) {
							return true;
						} else {
							return false;
						}
					})
					.collect(Collectors.toList()).get(0).getName();
			vo.setCatelogName(catlogName);
			vos.add(vo);
		}
		return new PageUtils(vos, Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);


	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public boolean saveAttr(AttrVo attrVo) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attrVo, attrEntity);
		int count = this.baseMapper.insert(attrEntity);
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
		vo.setCategoryPath(categoryService.findCategoryPath(attrEntity.getCatelogId()));
		return vo;
	}
}