package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.service.ProductFeignRemoteClient;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.ware.dao.PurchaseDao;
import element.io.mall.ware.entity.PurchaseDetailEntity;
import element.io.mall.ware.entity.PurchaseEntity;
import element.io.mall.ware.entity.WareSkuEntity;
import element.io.mall.ware.service.PurchaseDetailService;
import element.io.mall.ware.service.PurchaseService;
import element.io.mall.ware.service.WareSkuService;
import element.io.mall.ware.vo.Item;
import element.io.mall.ware.vo.PurchaseMergeVo;
import element.io.mall.ware.vo.PurchaseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.PurchaseDetailStatusEnum.OVER;
import static element.io.mall.common.enumerations.PurchaseStatusEnum.*;


@Slf4j
@SuppressWarnings({"all"})
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


	@Resource
	PurchaseDetailService purchaseDetailService;


	@Resource
	private WareSkuService wareSkuService;

	@Resource
	ProductFeignRemoteClient productFeignRemoteClient;

	/**
	 * &page=1&limit=10&key=
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		//Integer status = Objects.nonNull(params.get("status")) && !params.get("status").equals("") ? Integer.parseInt(params.get("status").toString()) : null;
		//Integer wareId = Objects.nonNull(params.get("wareId")) && !params.get("wareId").equals("") ? Integer.parseInt(params.get("wareId").toString()) : null;
		Page<PurchaseEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(StringUtils.hasText(key), PurchaseEntity::getId, key)
				.or(StringUtils.hasText(key), c -> c.eq(PurchaseEntity::getWareId, key));
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public void merge(PurchaseMergeVo vo) {
		log.info("接收到的参数{}", vo);
		Long purchaseId = vo.getPurchaseId();
		if (Objects.isNull(purchaseId)) {
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setCreateTime(new Date());
			purchaseEntity.setUpdateTime(new Date());
			purchaseEntity.setStatus(CREATED.getCode());
			this.baseMapper.insert(purchaseEntity);
			purchaseId = purchaseEntity.getId();
		}
		PurchaseEntity purchaseEntity = this.baseMapper
				.selectOne(new LambdaQueryWrapper<PurchaseEntity>().eq(PurchaseEntity::getId, purchaseId));
		if (Objects.nonNull(purchaseEntity)) {
			List<Long> detailItemIds = vo.getItems();
			if (CollectionUtils.isEmpty(detailItemIds)) {
				return;
			}
			Long finalPurchaseId = purchaseId;
			List<PurchaseDetailEntity> detailEntityList = detailItemIds.stream().map(item -> {
				PurchaseDetailEntity entity = new PurchaseDetailEntity();
				entity.setId(item);
				entity.setPurchaseId(finalPurchaseId);
				entity.setStatus(ASSIGNED.getCode());
				return entity;
			}).collect(Collectors.toList());
			purchaseDetailService.batchUpdatePurchaseDetailInfo(detailEntityList);
		} else {
			PurchaseMergeVo mergeVo = new PurchaseMergeVo();
			mergeVo.setItems(vo.getItems());
			this.merge(mergeVo);
		}

	}


	@Override
	public PageUtils queryUnreceiveListForPage(Map<String, Object> params) {
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Page<PurchaseEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(PurchaseEntity::getStatus, 0)
				.or()
				.eq(PurchaseEntity::getStatus, 1);
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public boolean receivePurchaseOrder(Long id) {
		PurchaseEntity entity = this.baseMapper.selectById(id);
		if (entity.getStatus().equals(RECEIVED.getCode()) || entity.getStatus().equals(FINISHED.getCode()) || entity.getStatus().equals(FAILED.getCode())) {
			return false;
		}
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(id);
		purchaseEntity.setStatus(RECEIVED.getCode());
		purchaseEntity.setUpdateTime(new Date());
		this.baseMapper.updateById(purchaseEntity);
		purchaseDetailService.updatePurchaseDetailStatus(id);
		return true;

	}


	@Override
	public void savePurchaseTaskProgress(PurchaseVo purchaseVo) {
		Long purchaseId = purchaseVo.getPurchaseId();
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(purchaseId);

		List<Item> items = purchaseVo.getItems();
		AtomicBoolean flag = new AtomicBoolean(true);
		List<PurchaseDetailEntity> detailEntities = items.stream().map(item -> {
			PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
			purchaseDetailEntity.setId(item.getItemId());
			purchaseDetailEntity.setStatus(item.getStatus());
			if (item.getStatus().equals(FAILED.getCode())) {
				flag.set(false);
			}
			return purchaseDetailEntity;
		}).collect(Collectors.toList());
		purchaseEntity.setStatus(flag.get() ? OVER.getCode() : FAILED.getCode());
		purchaseEntity.setUpdateTime(new Date());
		purchaseDetailService.updateBatchById(detailEntities);
		this.baseMapper.updateById(purchaseEntity);

		List<Long> detailIds = detailEntities.stream().map(e -> e.getId()).collect(Collectors.toList());
		List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.batchFindPurchaseDetailInfo(detailIds);
		List<WareSkuEntity> wareSkuEntities = purchaseDetailEntities.stream().map(i -> {
			WareSkuEntity wareSkuEntity = new WareSkuEntity();
			wareSkuEntity.setSkuId(i.getSkuId());
			wareSkuEntity.setWareId(i.getWareId());
			wareSkuEntity.setStock(i.getStatus().equals(OVER.getCode()) ? i.getSkuNum() : 0);
			wareSkuEntity.setStockLocked(0);
			R r = productFeignRemoteClient.info(i.getSkuId());
			Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
			wareSkuEntity.setSkuName(skuInfo.get("skuName").toString());
			log.info("调用的远程服务的结果{}", skuInfo);
			return wareSkuEntity;
		}).collect(Collectors.toList());
		wareSkuService.batchAddRecord(wareSkuEntities);
	}


}