package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.ex.NoStockException;
import element.io.mall.common.ex.UnlockException;
import element.io.mall.common.msg.ReleaseStockTaskTo;
import element.io.mall.common.msg.StockLockedTo;
import element.io.mall.common.to.SkuStockVo;
import element.io.mall.common.to.StockLockTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareSkuDao;
import element.io.mall.ware.entity.AvailableWare;
import element.io.mall.ware.entity.WareOrderTaskDetailEntity;
import element.io.mall.ware.entity.WareOrderTaskEntity;
import element.io.mall.ware.entity.WareSkuEntity;
import element.io.mall.ware.service.WareOrderTaskDetailService;
import element.io.mall.ware.service.WareOrderTaskService;
import element.io.mall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.MQConstants.DELAY_QUEUE_ROUTING_KEY;
import static element.io.mall.common.enumerations.MQConstants.STOCK_EVENT_EXCHANGE;

@Slf4j
@SuppressWarnings({"all"})
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


	@Resource
	private WareOrderTaskService wareOrderTaskService;

	@Resource
	private WareOrderTaskDetailService wareOrderTaskDetailService;

	@Resource
	private AmqpTemplate amqpTemplate;


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


	@Override
	public List<WareSkuEntity> queryGoodsStock(List<Long> ids) {
		LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.select(WareSkuEntity::getSkuId, WareSkuEntity::getStock)
				.in(WareSkuEntity::getSkuId, ids);
		return this.list(wrapper);
	}

	@Transactional(rollbackFor = {Throwable.class})
	@Override
	public boolean lockStock(List<StockLockTo> tos) {
		// 确保tos是一个非空集合就好
		String orderSn = tos.get(0).getOrderSn();
		WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
		wareOrderTaskEntity.setOrderSn(orderSn);
		wareOrderTaskEntity.setCreateTime(new Date());
		wareOrderTaskService.save(wareOrderTaskEntity);
		// 准备要发送的消息
		StockLockedTo stockLockedTo = new StockLockedTo();
		stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
		List<AvailableWare> availableWares = tos.stream().map(e -> {
			AvailableWare availableWare = new AvailableWare();
			availableWare.setSkuId(e.getSkuId());
			availableWare.setCount(e.getLockCount());
			availableWare.setWareIds(this.baseMapper.selectAvailableWares(e.getSkuId()));
			return availableWare;
		}).collect(Collectors.toList());
		// 队列消息
		ArrayList<ReleaseStockTaskTo> taskTos = new ArrayList<>();
		// 入库
		ArrayList<WareOrderTaskDetailEntity> detailEntities = new ArrayList<>();
		for (AvailableWare availableWare : availableWares) {
			boolean flag = false;
			if (CollectionUtils.isEmpty(availableWare.getWareIds())) {
				//	 没有库存信息,直接抛异常结束
				throw new NoStockException(availableWare.getSkuId());
			} else {
				for (Long wareId : availableWare.getWareIds()) {
					int num = this.baseMapper.lockStock(availableWare.getSkuId(), availableWare.getCount(), wareId);
					if (num == 1) {
						flag = true;
						WareOrderTaskDetailEntity detail = new WareOrderTaskDetailEntity();
						detail.setSkuId(availableWare.getSkuId());
						detail.setLockStatus(ReleaseStockTaskTo.LOCKED);
						detail.setWareId(wareId);
						detail.setSkuNum(availableWare.getCount());
						detail.setTaskId(wareOrderTaskEntity.getId());
						detailEntities.add(detail);
						ReleaseStockTaskTo task = new ReleaseStockTaskTo();
						BeanUtils.copyProperties(detail, task);
						taskTos.add(task);
						break;
					}
				}
				if (!flag) {
					throw new NoStockException(availableWare.getSkuId());
				}
			}
		}
		wareOrderTaskDetailService.saveBatch(detailEntities);
		stockLockedTo.setReleaseTos(taskTos);
		// 保存任务单详情并发送消息
		amqpTemplate.convertAndSend(STOCK_EVENT_EXCHANGE, DELAY_QUEUE_ROUTING_KEY, stockLockedTo);
		return true;
	}

	@Transactional(rollbackFor = {Throwable.class})
	@Override
	public boolean unlockStock(StockLockedTo to) {
		List<ReleaseStockTaskTo> tos = to.getReleaseTos();
		// 如果WareOrderTask不存在,详情单自然也不会存在,所以这里应该不需要判空了
		List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService.getTaskItems(to.getTaskId());
		// 解锁每一个详情单的库存
		for (WareOrderTaskDetailEntity detail : detailEntities) {
			if (detail.getLockStatus() != 2) {
				int count = this.baseMapper.unlock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum());
				if (count != 1) {
					throw new UnlockException(detail);
				}
				detail.setLockStatus(2);
			}
		}
		// 更新详情单的状态
		boolean flag = wareOrderTaskDetailService.updateBatchById(detailEntities);
		if (!flag) {
			throw new UnlockException("更新任务单状态失败" + detailEntities);
		}
		return true;
	}


	@Transactional(rollbackFor = {Throwable.class})
	@Override
	public boolean subStock(String orderSn) {
		log.info("接收到了扣减库存的消息{}", orderSn);
		LambdaQueryWrapper<WareOrderTaskEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(WareOrderTaskEntity::getOrderSn, orderSn);
		WareOrderTaskEntity taskEntity = wareOrderTaskService.getOne(wrapper);
		List<WareOrderTaskDetailEntity> taskItems = wareOrderTaskDetailService.getTaskItems(taskEntity.getId());
		List<Long> wareIds = taskItems.stream().map(e -> e.getWareId()).collect(Collectors.toList());
		LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(WareSkuEntity::getWareId, wareIds);
		List<WareSkuEntity> skuEntities = this.list(queryWrapper);
		List<WareOrderTaskDetailEntity> finalTaskItems = taskItems;
		List<WareSkuEntity> collect = skuEntities.stream()
				.map(ele -> {

					WareSkuEntity ware = new WareSkuEntity();
					WareOrderTaskDetailEntity detail = finalTaskItems.stream()
							.filter(i -> i.getWareId().equals(ele.getWareId()) && i.getSkuId().equals(ele.getSkuId()))
							.findFirst().get();
					ware.setStock(ele.getStock() - detail.getSkuNum());
					ware.setId(ele.getId());
					return ware;
				}).collect(Collectors.toList());
		List<WareOrderTaskDetailEntity> update = taskItems.stream().map(element -> {
			WareOrderTaskDetailEntity detail = new WareOrderTaskDetailEntity();
			detail.setId(element.getId());
			detail.setLockStatus(3);
			return detail;
		}).collect(Collectors.toList());
		// 更新库存单详情的状态
		wareOrderTaskDetailService.updateBatchById(update);
		// 解锁掉库存
		StockLockedTo to = new StockLockedTo();
		List<ReleaseStockTaskTo> releaseStockTaskTos = taskItems.stream().map(f -> {
			ReleaseStockTaskTo stockTaskTo = new ReleaseStockTaskTo();
			stockTaskTo.setTaskId(taskEntity.getId());
			stockTaskTo.setWareId(f.getWareId());
			stockTaskTo.setSkuNum(f.getSkuNum());
			stockTaskTo.setLockStatus(3);
			return stockTaskTo;
		}).collect(Collectors.toList());
		to.setReleaseTos(releaseStockTaskTos);
		to.setTaskId(taskEntity.getId());
		unlockStock(to);
		// 扣减库存
		return this.updateBatchById(collect);
	}


}