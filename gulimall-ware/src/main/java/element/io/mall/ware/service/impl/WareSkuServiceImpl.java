package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareSkuDao;
import element.io.mall.ware.entity.WareSkuEntity;
import element.io.mall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"all"})
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Integer status = Objects.nonNull(params.get("status")) && !params.get("status").equals("") ? Integer.parseInt(params.get("status").toString()) : null;
		Integer wareId = Objects.nonNull(params.get("wareId")) && !params.get("wareId").equals("") ? Integer.parseInt(params.get("wareId").toString()) : null;
		
		return null;
	}

}