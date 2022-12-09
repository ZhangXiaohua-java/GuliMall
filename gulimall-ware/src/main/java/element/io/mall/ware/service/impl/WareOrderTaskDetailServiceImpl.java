package element.io.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.dao.WareOrderTaskDetailDao;
import element.io.mall.ware.entity.WareOrderTaskDetailEntity;
import element.io.mall.ware.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public List<WareOrderTaskDetailEntity> getTaskItems(Long taskId) {
		return this.baseMapper.selectList(new LambdaQueryWrapper<WareOrderTaskDetailEntity>().eq(WareOrderTaskDetailEntity::getTaskId, taskId));
	}
	

}