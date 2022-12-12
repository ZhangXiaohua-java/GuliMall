package element.io.secskill.component;

import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.enumerations.SessionConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

	public static final ThreadLocal<MemberEntity> THREAD_LOCAL = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		if (Objects.isNull(session)) {
			response.sendRedirect("http://auth.gulimall.com/login.html");
			return false;
		}
		MemberEntity member = (MemberEntity) session.getAttribute(SessionConstants.LOGIN_USER);
		if (Objects.isNull(member)) {
			response.sendRedirect("http://auth.gulimall.com/login.html");
			return false;
		}
		THREAD_LOCAL.set(member);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		THREAD_LOCAL.remove();
	}


}
