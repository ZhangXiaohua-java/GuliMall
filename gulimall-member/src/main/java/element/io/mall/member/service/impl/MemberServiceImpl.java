package element.io.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.ex.PhoneOrNameHasRegistedException;
import element.io.mall.common.to.LoginTo;
import element.io.mall.common.to.OauthLoginTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.dao.MemberDao;
import element.io.mall.member.entity.MemberEntity;
import element.io.mall.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}


	@Override
	public boolean saveRegistedMemberInfo(MemberEntity member) {
		String username = member.getUsername();
		String mobile = member.getMobile();
		checkPhoneOrUsernameHasRegisted(mobile, username);
		member.setCreateTime(new Date());
		member.setLevelId(1L);
		String password = member.getPassword();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(password));
		return this.baseMapper.insert(member) == 1;
	}

	@Override
	public void checkPhoneOrUsernameHasRegisted(String phone, String username) {
		LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MemberEntity::getMobile, phone)
				.or().eq(MemberEntity::getUsername, username);
		MemberEntity entity = this.baseMapper.selectOne(wrapper);
		if (Objects.nonNull(entity)) {
			throw new PhoneOrNameHasRegistedException();
		}
	}


	@Override
	public MemberEntity memberLogin(LoginTo loginTo) {
		LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(MemberEntity::getUsername, loginTo.getAccount())
				.or().eq(MemberEntity::getMobile, loginTo.getAccount());
		MemberEntity member = this.baseMapper.selectOne(wrapper);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (Objects.nonNull(member) && passwordEncoder.matches(loginTo.getPassword(), member.getPassword())) {
			return member;
		}
		return null;
	}

	@Override
	public MemberEntity oauthLogin(OauthLoginTo to) {
		log.info("接收到的参数{}", to);
		Long uid = to.getId();
		MemberEntity member = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getSocialUid, uid));
		// 如果为空,则证明是首次注册
		if (Objects.isNull(member)) {
			log.info("当前用户是首次注册...");
			MemberEntity entity = new MemberEntity();
			entity.setSocialUid(to.getId().toString());
			entity.setUsername(to.getName());
			entity.setAccessToken(to.getAccess_token());
			entity.setExpiresIn(to.getExpires_in());
			this.baseMapper.insert(entity);
			return entity;
		} else {
			log.info("当前用户已经注册过了,更新数据...");
			member.setAccessToken(to.getAccess_token());
			member.setExpiresIn(to.getExpires_in());
			this.baseMapper.updateById(member);
		}
		return member;
	}


}