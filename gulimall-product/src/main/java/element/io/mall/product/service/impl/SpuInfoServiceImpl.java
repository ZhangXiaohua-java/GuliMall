package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SpuInfoDao;
import element.io.mall.product.entity.SpuInfoEntity;
import element.io.mall.product.service.SpuInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}