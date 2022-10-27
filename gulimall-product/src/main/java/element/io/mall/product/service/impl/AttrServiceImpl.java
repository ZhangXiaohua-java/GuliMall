package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrDao;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}