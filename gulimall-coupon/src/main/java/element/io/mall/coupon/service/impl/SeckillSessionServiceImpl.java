package element.io.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.to.SeckillSessionTo;
import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.dao.SeckillSessionDao;
import element.io.mall.coupon.entity.SeckillSessionEntity;
import element.io.mall.coupon.entity.SeckillSkuRelationEntity;
import element.io.mall.coupon.service.SeckillSessionService;
import element.io.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Resource
	private SeckillSkuRelationService seckillSkuRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Page<SeckillSessionEntity> page = new Page<>(pageNum, pageSize);
		this.page(page, null);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public List<SeckillSessionTo> getLatestSecKillProducts() {
		LambdaQueryWrapper<SeckillSessionEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.between(SeckillSessionEntity::getStartTime, getStartTimeOfToday(), getEndTimeOfTtDay());
		List<SeckillSessionEntity> seckillSessionEntities = this.list(wrapper);
		List<Long> sessionIds = seckillSessionEntities.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
		Map<Long, List<SeckillSkuRelationEntity>> map = seckillSkuRelationService.batchQueryRelations(sessionIds);
		return seckillSessionEntities.stream().map(e -> {
			SeckillSessionTo session = new SeckillSessionTo();
			BeanUtils.copyProperties(e, session);
			List<SeckillSkuRelationEntity> relationEntities = map.get(e.getId());
			List<SeckillSkuRelationTo> relations = relationEntities.stream().map(i -> {
				SeckillSkuRelationTo relation = new SeckillSkuRelationTo();
				BeanUtils.copyProperties(i, relation);
				return relation;
			}).collect(Collectors.toList());

			session.setRelations(relations);
			return session;
		}).collect(Collectors.toList());
	}

	private String getStartTimeOfToday() {
		return dateTimeFormatter.format(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
	}

	private String getEndTimeOfTtDay() {
		return dateTimeFormatter.format(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX));
	}

}