package element.io;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import element.io.cos.TencentCosConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@EnableFeignClients(basePackages = {"element.io"})
@EnableDiscoveryClient
@SpringBootApplication
public class TpsApplication {


	@Resource
	private COSClient cosClient;

	@Resource
	private TencentCosConfig tencentCosConfig;

	public static void main(String[] args) {
		SpringApplication.run(TpsApplication.class, args);
	}


	public void run(String... args) throws Exception {
		String filePath = "D:\\data\\壁纸\\绘梨衣.jpg";
		File file = new File(filePath);
		String bucketName = tencentCosConfig.getBucketName();
		String filename = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		filename = filename + "绘梨衣.jpg";
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, file);
		cosClient.putObject(putObjectRequest);
	}


}
