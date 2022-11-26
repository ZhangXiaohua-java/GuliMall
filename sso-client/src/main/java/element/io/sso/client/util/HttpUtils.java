package element.io.sso.client.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
public final class HttpUtils {

	private static final PoolingHttpClientConnectionManager connectionManager;

	static {
		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(10);
		connectionManager.setDefaultMaxPerRoute(3);
	}

	public static String getUserInfo(final String code) throws IOException {
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				.setMaxConnPerRoute(3).build();
		HttpGet request = new HttpGet("http://www.sso.com/user/info/" + code);
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String res = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
			return res;
		}
		return null;
	}

}
