package element.io.mall.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.dao.MemberLevelDao;
import element.io.mall.member.entity.MemberLevelEntity;
import element.io.mall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		
		return null;
	}

}