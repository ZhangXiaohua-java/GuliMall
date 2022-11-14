package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.ProductAttrValueDao;
import element.io.mall.product.entity.ProductAttrValueEntity;
import element.io.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public void batchSaveAttrs(List<ProductAttrValueEntity> productAttrValueEntities) {
		if (CollectionUtils.isEmpty(productAttrValueEntities)) {
			return;
		}
		this.saveBatch(productAttrValueEntities);
	}

	@Override
	public List<ProductAttrValueEntity> queryAttrsForSpu(Long spuId) {
		LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
		List<ProductAttrValueEntity> productAttrValueEntities = this.baseMapper.selectList(wrapper);
		return productAttrValueEntities;
	}

	@Override
	public void updateSpuAttrs(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities) {
		if (Objects.isNull(spuId) || CollectionUtils.isEmpty(productAttrValueEntities)) {
			return;
		}
		List<Long> ids = productAttrValueEntities.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
		this.baseMapper.deleteBatchIds(ids);
		this.saveOrUpdateBatch(productAttrValueEntities);
	}

	@Override
	public List<ProductAttrValueEntity> queryAttrsBySpuId(Long spuId) {
		LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
		return this.baseMapper.selectList(wrapper);
	}
	

}