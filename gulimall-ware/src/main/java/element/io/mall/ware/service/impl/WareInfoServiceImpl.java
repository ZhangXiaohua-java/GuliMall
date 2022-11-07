package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareInfoDao;
import element.io.mall.ware.entity.WareInfoEntity;
import element.io.mall.ware.service.WareInfoService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		

		return null;
	}

}