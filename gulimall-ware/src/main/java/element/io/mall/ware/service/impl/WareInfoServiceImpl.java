package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareInfoDao;
import element.io.mall.ware.entity.WareInfoEntity;
import element.io.mall.ware.service.WareInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"all"})
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Page<WareInfoEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(StringUtils.hasText(key), WareInfoEntity::getId, key)
				.or()
				.eq(StringUtils.hasText(key), WareInfoEntity::getAreacode, key)
				.or()
				.like(StringUtils.hasText(key), WareInfoEntity::getName, key);
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


}