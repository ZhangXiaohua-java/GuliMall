package element.io.mall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;


import element.io.mall.member.dao.MemberReceiveAddressDao;
import element.io.mall.member.entity.MemberReceiveAddressEntity;
import element.io.mall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}