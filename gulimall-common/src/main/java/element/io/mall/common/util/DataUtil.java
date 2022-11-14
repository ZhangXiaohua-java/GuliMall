package element.io.mall.common.util;

import java.util.Objects;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
public final class DataUtil {

	/**
	 * @param num 目标值
	 * @return 为空则返回0, 不为空则返回原本的值
	 */
	public static final Integer ifNull(Integer num) {
		if (Objects.isNull(num)) {
			return 0;
		} else {
			return num;
		}
	}


}
