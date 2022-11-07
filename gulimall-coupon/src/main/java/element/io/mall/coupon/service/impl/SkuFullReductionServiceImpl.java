package element.io.mall.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.to.MemberPrice;
import element.io.mall.common.to.SkuReductionTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.dao.SkuFullReductionDao;
import element.io.mall.coupon.entity.MemberPriceEntity;
import element.io.mall.coupon.entity.SkuFullReductionEntity;
import element.io.mall.coupon.service.MemberPriceService;
import element.io.mall.coupon.service.SkuFullReductionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

	@Resource
	private SkuFullReductionService skuFullReductionService;


	@Resource
	private MemberPriceService memberPriceService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public boolean saveReduction(SkuReductionTo skuReductionTo) {
		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		skuFullReductionEntity.setSkuId(skuReductionTo.getSkuId());
		skuFullReductionEntity.setFullPrice(skuReductionTo.getFullPrice());
		skuFullReductionEntity.setReducePrice(skuReductionTo.getReducePrice());
		skuFullReductionEntity.setAddOther(skuFullReductionEntity.getAddOther());
		this.saveBasicReductionInfo(skuFullReductionEntity);
		// ----------------------------------------
		SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
		reductionEntity.setSkuId(reductionEntity.getSkuId());
		reductionEntity.setFullPrice(reductionEntity.getFullPrice());
		reductionEntity.setReducePrice(skuReductionTo.getReducePrice());
		reductionEntity.setAddOther(skuReductionTo.getCountStatus());
		skuFullReductionService.save(reductionEntity);
		List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
		if (memberPrice != null && memberPrice.size() != 0) {
			List<MemberPriceEntity> memberPriceEntities = memberPrice.stream().map(price -> {
				MemberPriceEntity priceEntity = new MemberPriceEntity();
				priceEntity.setSkuId(skuReductionTo.getSkuId());
				priceEntity.setAddOther(skuReductionTo.getCountStatus());
				priceEntity.setMemberLevelId(price.getId());
				priceEntity.setMemberLevelName(price.getName());
				priceEntity.setMemberPrice(price.getPrice());
				return priceEntity;
			}).collect(Collectors.toList());
			memberPriceService.saveBatch(memberPriceEntities);
		}

		return true;
	}

	@Override
	public void saveBasicReductionInfo(SkuFullReductionEntity skuFullReductionEntity) {
		this.save(skuFullReductionEntity);
	}

}