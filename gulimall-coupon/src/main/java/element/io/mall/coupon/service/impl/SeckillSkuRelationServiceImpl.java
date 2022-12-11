package element.io.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.dao.SeckillSkuRelationDao;
import element.io.mall.coupon.entity.SeckillSkuRelationEntity;
import element.io.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Long promotionSessionId = Long.valueOf(params.get("promotionSessionId").toString());
		Page<SeckillSkuRelationEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<SeckillSkuRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SeckillSkuRelationEntity::getPromotionSessionId, promotionSessionId);
		this.page(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}

	@Override
	public Map<Long, List<SeckillSkuRelationEntity>> batchQueryRelations(List<Long> sessionIds) {
		if (CollectionUtils.isEmpty(sessionIds)) {
			return null;
		}
		LambdaQueryWrapper<SeckillSkuRelationEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(SeckillSkuRelationEntity::getPromotionSessionId, sessionIds);
		List<SeckillSkuRelationEntity> relationEntities = this.list(wrapper);
		Map<Long, List<SeckillSkuRelationEntity>> map = relationEntities.stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId, Collectors.toList()));
		return map;
	}


}