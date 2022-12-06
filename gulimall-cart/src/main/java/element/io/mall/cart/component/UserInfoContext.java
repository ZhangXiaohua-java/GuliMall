package element.io.mall.cart.component;

import element.io.mall.cart.vo.UserInfo;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
public final class UserInfoContext {

	public static final ThreadLocal<UserInfo> CONTEXT = new ThreadLocal<>();

	public static ThreadLocal<UserInfo> currentContext() {
		return CONTEXT;
	}
	
}
