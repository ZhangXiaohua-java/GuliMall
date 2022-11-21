package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SkuImagesDao;
import element.io.mall.product.entity.SkuImagesEntity;
import element.io.mall.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public List<SkuImagesEntity> getSkuImagesBySkuId(Long skuId) {
		LambdaQueryWrapper<SkuImagesEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SkuImagesEntity::getSkuId, skuId);
		return this.baseMapper.selectList(queryWrapper);
	}
	

}