package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SpuInfoDescDao;
import element.io.mall.product.entity.SpuInfoDescEntity;
import element.io.mall.product.service.SpuInfoDescService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public void saveDesc(SpuInfoDescEntity spuInfoDescEntity) {
		this.baseMapper.insert(spuInfoDescEntity);
	}

	@Override
	public SpuInfoDescEntity getSpuDescBySpuId(Long spuId) {
		return this.baseMapper.selectOne(new LambdaQueryWrapper<SpuInfoDescEntity>().eq(SpuInfoDescEntity::getSpuId, spuId));
	}
	
}