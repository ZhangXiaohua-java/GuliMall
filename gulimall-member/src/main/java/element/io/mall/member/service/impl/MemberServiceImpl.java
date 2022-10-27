package element.io.mall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;


import element.io.mall.member.dao.MemberDao;
import element.io.mall.member.entity.MemberEntity;
import element.io.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}