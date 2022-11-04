package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.AttrAttrgroupRelationDao;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;
import element.io.mall.product.service.AttrAttrgroupRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public boolean batchRemoveRelations(List<AttrAttrgroupRelationEntity> relationEntities) {
		int count = this.baseMapper.batchDeleteRelations(relationEntities);
		return count == relationEntities.size();
	}
	

}