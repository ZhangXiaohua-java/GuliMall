package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.BrandDao;
import element.io.mall.product.entity.BrandEntity;
import element.io.mall.product.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}