package element.io.mall.auth.web;

import com.alibaba.fastjson.TypeReference;
import element.io.mall.auth.service.ISmsService;
import element.io.mall.auth.vo.LoginVo;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.enumerations.ExceptionStatusEnum;
import element.io.mall.common.enumerations.SessionConstants;
import element.io.mall.common.service.MemberFeignRemoteClient;
import element.io.mall.common.to.LoginTo;
import element.io.mall.common.to.RegisterTo;
import element.io.mall.common.util.DataUtil;
import element.io.mall.common.util.R;
import element.io.mall.common.vo.RegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@RestController
@Slf4j
public class AuthController {


	@Resource
	private ISmsService smsService;

	@Resource
	private MemberFeignRemoteClient memberFeignRemoteClient;

	@PostMapping("/sms/code")
	public R sendCode(String phone, HttpServletRequest request) {
		String ip = request.getRemoteAddr();

		if (smsService.sendCode(phone, ip)) {
			return R.ok();
		} else {
			return R.excepion(ExceptionStatusEnum.CODE_REQUEST_FREQUENTLY_EXCEPTION);
		}
	}


	// 使用JSR303的参数校验
	@PostMapping("/register")
	public R register(@Validated @RequestBody RegisterVo registerVo) {
		log.info("接收到的注册信息{}", registerVo);
		boolean flag = smsService.verifyCode(registerVo.getPhone(), registerVo.getVerifyCode());
		if (!flag) {
			return R.excepion(ExceptionStatusEnum.VERIFY_CODE_NOT_MATCH_EXCEPTION);
		}
		// call remote service
		RegisterTo to = new RegisterTo();
		to.setMobile(registerVo.getPhone());
		to.setPassword(registerVo.getPassword());
		to.setUsername(registerVo.getUsername());
		R res = null;
		try {
			res = memberFeignRemoteClient.register(to);
		} catch (Exception e) {
			return R.error(e.getMessage());
		}
		if (res.get("code").toString().equals("0")) {
			return R.ok();
		} else {
			return R.error("请检查提交的参数是否有误");
		}
	}

	@PostMapping("/login")
	public R login(@RequestBody LoginVo loginVo, HttpSession httpSession) {
		log.info("接收到的登陆信息{}", loginVo);
		LoginTo to = new LoginTo();
		to.setAccount(loginVo.getAccount());
		to.setPassword(loginVo.getPassword());
		R r = memberFeignRemoteClient.login(to);
		if (!r.get("code").toString().equals("0")) {
			return R.error("请检查账户或者密码是否有误");
		}
		MemberEntity member = DataUtil.typeConvert(r.get("data"), new TypeReference<MemberEntity>() {
		});
		httpSession.setAttribute(SessionConstants.LOGIN_USER, member);
		log.info("用户{}登陆成功,会员信息{}", loginVo.getAccount(), member);
		return R.ok();
	}


}
