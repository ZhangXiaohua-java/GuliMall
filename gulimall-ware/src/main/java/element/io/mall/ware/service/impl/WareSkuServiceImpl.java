package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.to.SkuStockVo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareSkuDao;
import element.io.mall.ware.entity.WareSkuEntity;
import element.io.mall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Integer skuId = Objects.nonNull(params.get("skuId")) && !params.get("skuId").equals("") ? Integer.parseInt(params.get("status").toString()) : null;
		Integer wareId = Objects.nonNull(params.get("wareId")) && !params.get("wareId").equals("") ? Integer.parseInt(params.get("wareId").toString()) : null;
		Page<WareSkuEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Objects.nonNull(wareId), WareSkuEntity::getWareId, wareId)
				.and(Objects.nonNull(skuId), condition -> condition.eq(WareSkuEntity::getSkuId, skuId));
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}

	@Override
	public void batchAddRecord(List<WareSkuEntity> wareSkuEntities) {
		for (WareSkuEntity e : wareSkuEntities) {
			LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(WareSkuEntity::getWareId, e.getWareId())
					.and(condition -> condition.eq(WareSkuEntity::getSkuId, e.getSkuId()));
			WareSkuEntity wareSkuEntity = this.baseMapper.selectOne(wrapper);
			if (Objects.nonNull(wareSkuEntity)) {
				e.setStock(e.getStock() + wareSkuEntity.getStock());
				this.baseMapper.update(e, wrapper);
			} else {
				this.baseMapper.insert(e);
			}
		}

	}


	@Override
	public List<SkuStockVo> queryStock(Long[] skuIds) {

		List<SkuStockVo> stockVos = Arrays.asList(skuIds).stream().map(e -> {
			SkuStockVo vo = new SkuStockVo();
			vo.setSkuId(e);
			Long stock = this.baseMapper.selectStock(e);
			vo.setHasStock(stock > 0 ? true : false);
			return vo;
		}).collect(Collectors.toList());
		return stockVos;
	}


}