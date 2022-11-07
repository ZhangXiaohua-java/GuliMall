package element.io.mall.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.dao.MemberLoginLogDao;
import element.io.mall.member.entity.MemberLoginLogEntity;
import element.io.mall.member.service.MemberLoginLogService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLoginLogService")
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogDao, MemberLoginLogEntity> implements MemberLoginLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}