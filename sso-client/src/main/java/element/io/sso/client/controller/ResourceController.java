package element.io.sso.client.controller;

import element.io.sso.client.annotation.PermissionCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@RestController
public class ResourceController {

	@GetMapping("/hello")
	public String hello() {
		return "hello 当前访问的不是受保护的资源";
	}


	@GetMapping("/res")
	@PermissionCheck(needLogin = true)
	public String protectedResource() {
		return "登陆成功,当前资源仅VIP可以访问";
	}


}
