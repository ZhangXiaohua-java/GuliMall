package element.io.sso.server.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@SuppressWarnings({"all"})
@Controller
public class SsoController {

	@Resource
	private RedisTemplate redisTemplate;

	private ValueOperations ops;

	@PostConstruct
	public void init() {
		ops = redisTemplate.opsForValue();
	}

	@GetMapping("/login.html")
	public String toLogin(@RequestParam String redirect_url, @CookieValue(value = "token", required = false) String code, Model model) {
		if (StringUtils.hasText(code)) {
			Object res = ops.get("sso:" + code);
			if (Objects.nonNull(res) && StringUtils.hasText(res.toString())) {
				return "redirect:" + redirect_url + "?code=" + code;
			}
		}
		model.addAttribute("url", redirect_url);
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam(value = "url") String redirectUrl,
						@RequestParam String username, @RequestParam String password,
						HttpServletResponse response) {
		// 登陆检验逻辑处理
		if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
			String uuid = UUID.randomUUID().toString().replace("_", "");
			ops.set("sso:" + uuid, username + ":" + password, Duration.ofDays(7));
			Cookie cookie = new Cookie("token", uuid);
			response.addCookie(cookie);
			return "redirect:" + redirectUrl + "?code=" + uuid;
		}
		return "redirect:/login?redirect_url=" + redirectUrl;
	}

	@ResponseBody
	@GetMapping("/user/info/{code}")
	public String loginUserInfo(@PathVariable String code) {
		Object res = ops.get("sso:" + code);
		if (Objects.nonNull(res)) {
			return res.toString();
		}
		return "无效的code值";
	}


}
