package element.io.mall.order.component;

import com.alibaba.fastjson.TypeReference;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.enumerations.SessionConstants;
import element.io.mall.common.util.DataUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		String redirectUrl = "http://auth.gulimall.com/login.html?";
		redirectUrl += URLEncoder.encode("msg=请先登陆", "UTF-8");
		if (Objects.isNull(session)) {
			response.sendRedirect(redirectUrl);
			return false;
		}
		Object data = session.getAttribute(SessionConstants.LOGIN_USER);
		if (Objects.isNull(data)) {
			response.sendRedirect(redirectUrl);
			return false;
		}
		MemberEntity member = DataUtil.typeConvert(data, new TypeReference<MemberEntity>() {
		});
		UserInfoContext.currentContext().set(member);
		return true;
	}


}
