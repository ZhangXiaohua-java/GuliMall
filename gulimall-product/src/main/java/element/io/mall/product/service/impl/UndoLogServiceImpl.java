package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.UndoLogDao;
import element.io.mall.product.entity.UndoLogEntity;
import element.io.mall.product.service.UndoLogService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("undoLogService")
public class UndoLogServiceImpl extends ServiceImpl<UndoLogDao, UndoLogEntity> implements UndoLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}