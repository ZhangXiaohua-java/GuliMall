package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.ProductAttrValueDao;
import element.io.mall.product.entity.ProductAttrValueEntity;
import element.io.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}