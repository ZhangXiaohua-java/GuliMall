package element.io.mall.coupon.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.dao.SeckillPromotionDao;
import element.io.mall.coupon.entity.SeckillPromotionEntity;
import element.io.mall.coupon.service.SeckillPromotionService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service("seckillPromotionService")
public class SeckillPromotionServiceImpl extends ServiceImpl<SeckillPromotionDao, SeckillPromotionEntity> implements SeckillPromotionService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Page<SeckillPromotionEntity> page = new Page<>(pageNum, pageSize);
		//LambdaQueryWrapper<SeckillPromotionEntity> wrapper = new LambdaQueryWrapper<>();
		this.page(page, null);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}
	

}