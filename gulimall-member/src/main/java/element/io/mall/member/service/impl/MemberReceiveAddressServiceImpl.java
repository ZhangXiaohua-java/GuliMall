package element.io.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.dao.MemberReceiveAddressDao;
import element.io.mall.member.entity.MemberReceiveAddressEntity;
import element.io.mall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public List<MemberReceiveAddressEntity> queryMemberAllAddress(Long memberId) {
		LambdaQueryWrapper<MemberReceiveAddressEntity> wrapper = new LambdaQueryWrapper<MemberReceiveAddressEntity>()
				.eq(MemberReceiveAddressEntity::getMemberId, memberId);
		return this.list(wrapper);
	}
	

}