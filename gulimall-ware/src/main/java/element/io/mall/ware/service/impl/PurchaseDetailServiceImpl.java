package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.PurchaseDetailDao;
import element.io.mall.ware.entity.PurchaseDetailEntity;
import element.io.mall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.PurchaseDetailStatusEnum.DOING;


@SuppressWarnings({"all"})
@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Integer status = Objects.nonNull(params.get("status")) && !params.get("status").equals("") ? Integer.parseInt(params.get("status").toString()) : null;
		Integer wareId = Objects.nonNull(params.get("wareId")) && !params.get("wareId").equals("") ? Integer.parseInt(params.get("wareId").toString()) : null;
		Page<PurchaseDetailEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Objects.nonNull(wareId), PurchaseDetailEntity::getWareId, wareId)
				.eq(Objects.nonNull(status), PurchaseDetailEntity::getStatus, status)
				.or(StringUtils.hasText(key), condition -> condition.eq(PurchaseDetailEntity::getPurchaseId, key)
						.or()
						.eq(PurchaseDetailEntity::getSkuId, key)
						.or()
						.eq(PurchaseDetailEntity::getWareId, key)
				);
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public List<PurchaseDetailEntity> batchFindPurchaseDetailInfo(List<Long> detailItemIds) {
		return this.baseMapper.selectBatchIds(detailItemIds);
	}


	@Override
	public void batchUpdatePurchaseDetailInfo(List<PurchaseDetailEntity> detailEntities) {
		this.updateBatchById(detailEntities);
	}

	@Override
	public void updatePurchaseDetailStatus(Long id) {
		LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(PurchaseDetailEntity::getPurchaseId, id);
		List<PurchaseDetailEntity> detailEntities = this.baseMapper.selectList(wrapper);
		detailEntities = detailEntities.stream().map(item -> {
			item.setStatus(DOING.getCode());
			return item;
		}).collect(Collectors.toList());
		this.updateBatchById(detailEntities);
	}
	

}