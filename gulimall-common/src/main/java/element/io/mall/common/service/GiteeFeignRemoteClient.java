package element.io.mall.common.service;

import element.io.mall.common.to.GiteeRequestTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
@FeignClient(value = "gitee", url = "https://www.gitee.com")
public interface GiteeFeignRemoteClient {

	/**
	 * grant_type=authorization_code
	 * &code={code}
	 * client_id={client_id}
	 * redirect_uri={redirect_uri}
	 * client_secret={client_secret}
	 */

	@PostMapping("/oauth/token")
	String getToken(GiteeRequestTo to);


	@GetMapping("/api/v5/user")
	String getUserInfo(@RequestParam("access_token") String access_token);
	

}
