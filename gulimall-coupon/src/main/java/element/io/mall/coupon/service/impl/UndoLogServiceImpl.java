package element.io.mall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;


import element.io.mall.coupon.dao.UndoLogDao;
import element.io.mall.coupon.entity.UndoLogEntity;
import element.io.mall.coupon.service.UndoLogService;


@Service("undoLogService")
public class UndoLogServiceImpl extends ServiceImpl<UndoLogDao, UndoLogEntity> implements UndoLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}