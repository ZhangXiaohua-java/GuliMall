package element.io.mall.common.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
public final class CodeUtils {

	public static String generateRandomCode(int length) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(10);
			sb.append(num);
		}
		return sb.toString();
	}

	public static String randomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}


}
