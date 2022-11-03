package element.io.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author 张晓华
 * @date 2022-10-31
 */

@Configuration
public class TencentCosConfig {


	@Value("${tencent.secretId}")
	private String secretId;

	@Value("${tencent.secretKey}")
	private String secretKey;


	@Value("${tencent.bucket-name}")
	private String bucketName;

	// CosClient是线程安全的实例
	@Bean
	public COSClient tencentCosClient() {
		System.out.println(secretId);
		System.out.println(secretKey);
		System.out.println(bucketName);
		System.out.println("run");
		COSCredentials cosCredentials = new BasicCOSCredentials(secretId, secretKey);
		Region region = new Region("ap-nanjing");
		ClientConfig clientConfig = new ClientConfig(region);
		clientConfig.setHttpProtocol(HttpProtocol.http);
		// 设置签名的有效时间
		clientConfig.setSignExpired(180);
		COSClient cosClient = new COSClient(cosCredentials, clientConfig);
		return cosClient;
	}


	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
}
