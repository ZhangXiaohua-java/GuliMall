package element.io.mall.cart.component;

import com.alibaba.fastjson.TypeReference;
import element.io.mall.cart.vo.UserInfo;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.enumerations.CartConstants;
import element.io.mall.common.enumerations.SessionConstants;
import element.io.mall.common.util.DataUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Component
public class UserInfoInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserInfo info = new UserInfo();
		if (Objects.nonNull(request.getSession(false))) {
			// 已经登陆的用户
			MemberEntity member = DataUtil.typeConvert(request.getSession().getAttribute(SessionConstants.LOGIN_USER), new TypeReference<MemberEntity>() {
			});
			info.setUserId(member.getId());
		}
		//	 未登录
		Cookie[] cookies = request.getCookies();
		if (Objects.nonNull(cookies) && cookies.length > 0) {
			Arrays.stream(cookies)
					.filter(cookie -> CartConstants.USER_KEY.equals(cookie.getName()))
					.findAny().ifPresent(cook -> info.setKey(cook.getValue()));
		}
		if (!StringUtils.hasText(info.getKey())) {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			info.setKey(uuid);
			info.setNew(true);
		}
		UserInfoContext.currentContext().set(info);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		UserInfo userInfo = UserInfoContext.currentContext().get();
		if (userInfo.isNew()) {
			Cookie cookie = new Cookie(CartConstants.USER_KEY, userInfo.getKey());
			cookie.setDomain("gulimall.com");
			cookie.setMaxAge(2592000);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		if (userInfo.isDelete()) {
			Cookie cookie = new Cookie(CartConstants.USER_KEY, userInfo.getKey());
			cookie.setDomain("gulimall.com");
			cookie.setMaxAge(0);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// 将ThreadLocal中的值,防止在线程被销毁后,值依旧然在threadLocal中
		UserInfoContext.currentContext().remove();
	}


}
