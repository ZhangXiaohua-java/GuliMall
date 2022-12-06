package element.io.mall.order.component;

import element.io.mall.common.domain.MemberEntity;

/**
 * @author 张晓华
 * @date 2022-12-3
 */
public final class UserInfoContext {

	private static final ThreadLocal<MemberEntity> MEMBER_ENTITY_THREAD_LOCAL = new ThreadLocal();

	public static ThreadLocal<MemberEntity> currentContext() {
		return MEMBER_ENTITY_THREAD_LOCAL;
	}


}
