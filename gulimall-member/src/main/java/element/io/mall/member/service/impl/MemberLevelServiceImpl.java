package element.io.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.dao.MemberLevelDao;
import element.io.mall.member.entity.MemberLevelEntity;
import element.io.mall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : null;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 0;
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Page<MemberLevelEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(StringUtils.hasText(key), MemberLevelEntity::getId, key)
				.or(StringUtils.hasText(key)).like(MemberLevelEntity::getName, key);
		this.baseMapper.selectPage(page, wrapper);
		List<MemberLevelEntity> records = page.getRecords();
		PageUtils pageUtils = new PageUtils(records, Long.valueOf(page.getTotal()).intValue(), pageNum, pageSize);
		return pageUtils;
	}

}