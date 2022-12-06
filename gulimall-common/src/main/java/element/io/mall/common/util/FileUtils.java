package element.io.mall.common.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
public final class FileUtils {


	public static String readFileToString(InputStream inputStream) throws IOException {
		byte[] bytes = new byte[1024];
		int len = 0;
		StringBuffer sb = new StringBuffer();
		while ((len = inputStream.read(bytes)) != -1) {
			sb.append(new String(bytes, 0, len));
		}
		return sb.toString();
	}

}
