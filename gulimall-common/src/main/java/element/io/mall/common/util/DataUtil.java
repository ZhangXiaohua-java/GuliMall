package element.io.mall.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

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

	/**
	 * 类型转换工具
	 *
	 * @param data          要转换的数据
	 * @param typeReference 期望的数据类型
	 * @param <T>           要转转的目标类型
	 * @return 目标对象
	 */
	public static <T> T typeConvert(Object data, TypeReference<T> typeReference) {
		String str = JSON.toJSONString(data);
		return JSON.parseObject(str, typeReference);
	}

}
