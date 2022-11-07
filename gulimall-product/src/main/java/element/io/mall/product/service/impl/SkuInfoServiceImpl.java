package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SkuInfoDao;
import element.io.mall.product.entity.SkuInfoEntity;
import element.io.mall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"all"})
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : null;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : null;
		Long brandId = Objects.nonNull(params.get("brandId")) && !params.get("brandId").equals("0") ? Long.valueOf(params.get("brandId").toString()) : null;
		Long catelogId = Objects.nonNull(params.get("catelogId")) && !params.get("catelogId").equals("0") ? Long.valueOf(params.get("catelogId").toString()) : null;
		BigDecimal min = Objects.nonNull(params.get("min")) ? new BigDecimal(params.get("min").toString()) : null;
		BigDecimal max = Objects.nonNull(params.get("max")) && !params.get("max").equals("0") ? new BigDecimal(params.get("max").toString()) : null;
		Page<SkuInfoEntity> skuInfoEntityPage = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Objects.nonNull(catelogId), SkuInfoEntity::getCatalogId, catelogId)
				.and(Objects.nonNull(brandId), c -> c.eq(SkuInfoEntity::getBrandId, brandId))
				.and(Objects.nonNull(min), con -> con.ge(SkuInfoEntity::getPrice, min))
				.and(Objects.nonNull(max), cond -> cond.le(SkuInfoEntity::getPrice, max))
				.and(StringUtils.hasText(key), condition -> condition.like(SkuInfoEntity::getSkuName, key).eq(SkuInfoEntity::getSkuId, key));
		this.baseMapper.selectPage(skuInfoEntityPage, queryWrapper);
		return new PageUtils(skuInfoEntityPage.getRecords(), Long.valueOf(skuInfoEntityPage.getTotal()).intValue(), pageSize, pageNum);
	}
	

}