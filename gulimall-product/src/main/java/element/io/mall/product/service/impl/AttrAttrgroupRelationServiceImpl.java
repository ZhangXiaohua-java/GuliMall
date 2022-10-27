package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrAttrgroupRelationDao;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;
import element.io.mall.product.service.AttrAttrgroupRelationService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}