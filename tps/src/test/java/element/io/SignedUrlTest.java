package element.io;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author 张晓华
 * @date 2022-10-31
 */
public class SignedUrlTest {

	public static void tt(String[] args) throws IOException {
		String url = "http://gulimall-1257969783.cos.ap-nanjing.myqcloud.com/2022-10-31%7B%0D%0A%20%20%20%20%22filename%22%3A%20%22%E5%B0%8F%E7%B1%B3%E7%B2%A5.mp4%22%0D%0A%7D?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDRhiTzGIC3lLwPuDJCqHSREn7E6kGYcbM%26q-sign-time%3D1667210947%3B1667211127%26q-key-time%3D1667210947%3B1667211127%26q-header-list%3Dhost%26q-url-param-list%3D%26q-signature%3D68ac51a5fbdacac71b3ca11ac907b75a91047166";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpEntity fileEntity = new FileEntity(new File("D:\\xiaomizhou.mp4"));
		HttpPost post = new HttpPost(url);
		post.setEntity(fileEntity);
		//post.addHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
		CloseableHttpResponse httpResponse = httpClient.execute(post);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			System.out.println("OK");
		} else {
			System.out.println("ERROR");
			System.out.println("------------------------------------------");
			System.out.println(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
		}
	}

	public static void main(String[] args) throws IOException {
		any();
	}

	public static void any() throws IOException {
		// 以华东1（杭州）的外网Endpoint为例，其它Region请按实际情况填写。
		String endpoint = "oss-cn-qingdao.aliyuncs.com";
		// 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
		String accessKeyId = "LTAI5tRPQVUs35xAAVJKq9qc";
		String accessKeySecret = "f8VfJIGO9EDJz3wzHyOwEvboxUPQWk";
		// 从STS服务获取的安全令牌（SecurityToken）。
		String securityToken = "yourSecurityToken";
		// 填写Bucket名称，例如examplebucket。
		String bucketName = "guli-mall2022";
		// 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
		String objectName = "/2022-10-31/test.mp4";
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);
		// 设置签名URL过期时间，单位为毫秒。
		Date expiration = new Date(new Date().getTime() + 3600 * 1000);
		// 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
		URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
		System.out.println(url.toString());
		HttpPost post = new HttpPost(url.toString());
		post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpEntity entity = new FileEntity(new File("D:\\xiaomizhou.mp4"));
		
		post.setEntity(entity);
		CloseableHttpResponse httpResponse = httpClient.execute(post);
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			System.err.println("??????????????????");
		} else {
			System.out.println("OK");
			System.out.println(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
		}

	}


}
