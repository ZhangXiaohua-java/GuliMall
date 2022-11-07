package element.io.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import element.io.mall.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author 张晓华
 * @date 2022-10-31
 */
@Slf4j
@RestController
public class CosController {

	@Value("${tencent.bucket-name}")
	private String bucketName;

	@Resource
	private COSClient cosClient;

	@Resource
	private OSSClient ossClient;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	@Value("${spring.cloud.alicloud.access-key}")
	private String accessId;

	private static String aliBucketName = "guli-mall2022.oss-cn-qingdao.aliyuncs.com";


	@PostMapping("/sign")
	public Object sign(@RequestBody String fileName) {
		fileName = dateTimeFormatter.format(LocalDate.now()) + fileName;
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName, HttpMethodName.POST);
		URL url = cosClient.generatePresignedUrl(request);
		String path = url.getPath();
		String host = url.getHost();
		String authority = url.getAuthority();
		String query = url.getQuery();
		log.info("path{}, host {},authority{}, query{}", path, host, authority, query);
		return url;
	}

	@GetMapping("/alicloud")
	public R alibaba() {
		String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
		Date expireDate = new Date(System.currentTimeMillis() + 60 * 1000 * 3);
		String host = "https://" + aliBucketName;
		PolicyConditions policyConditions = new PolicyConditions();
		policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
		policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
		String postPolicy = ossClient.generatePostPolicy(expireDate, policyConditions);
		byte[] bytes = postPolicy.getBytes(StandardCharsets.UTF_8);
		String encodedPolicy = BinaryUtil.toBase64String(bytes);
		String signature = ossClient.calculatePostSignature(postPolicy);
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("policy", encodedPolicy);
		map.put("signature", signature);
		map.put("host", host);
		map.put("expire", String.valueOf(expireDate.getTime() / 1000));
		map.put("accessId", accessId);
		map.put("dir", dir);
		return R.ok().put("data", map);
	}


}
