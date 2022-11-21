package element.io.mall.auth.web;

import element.io.mall.auth.service.ISmsService;
import element.io.mall.common.enumerations.ExceptionStatusEnum;
import element.io.mall.common.util.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@RestController(value = "/auth")
public class AuthController {


	@Resource
	private ISmsService smsService;

	@PostMapping("/sms/code")
	public R sendCode(String phone, HttpServletRequest request) {
		String ip = request.getRemoteAddr();

		if (smsService.sendCode(phone, ip)) {
			return R.ok();
		} else {
			return R.excepion(ExceptionStatusEnum.CODE_REQUEST_FREQUENTLY_EXCEPTION);
		}
	}


}
