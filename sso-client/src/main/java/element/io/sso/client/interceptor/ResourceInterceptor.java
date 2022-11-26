package element.io.sso.client.interceptor;

import element.io.sso.client.annotation.PermissionCheck;
import element.io.sso.client.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Slf4j
@Component
public class ResourceInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String url = request.getRequestURL().toString();
		log.info("拦截到的请求地址{}", url);
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap.containsKey("code")) {
			if (parameterMap.get("code")[0] == null || "".equals(parameterMap.get("code")[0])) {
				return false;
			} else {
				String code = parameterMap.get("code")[0];
				String userInfo = HttpUtils.getUserInfo(code);
				if (StringUtils.hasText(userInfo)) {
					request.getSession().setAttribute("user", userInfo);
					log.info("创建了session,用户信息{}", userInfo);
					// 直接改变浏览器地址栏中的地址,让用户无感知code的存在
					response.sendRedirect(url);
					return true;
				} else {
					return false;
				}
			}
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		log.info("名字{}", handlerMethod.getMethod().getName());
		if (handlerMethod.getMethod().isAnnotationPresent(PermissionCheck.class)) {
			HttpSession session = request.getSession(false);
			if (Objects.isNull(session) || Objects.isNull(session.getAttribute("user"))) {
				// 重定向
				response.sendRedirect("http://www.sso.com/login.html?redirect_url=" + url);
				log.info("重定向了请求");
				return false;
			}
			log.info("放行了请求");
			return true;
		} else {
			log.info("访问不受保护的资源直接放行");
			return true;
		}
	}

}
