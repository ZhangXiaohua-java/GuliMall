package element.io.mall.auth.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.enumerations.SessionConstants;
import element.io.mall.common.service.GiteeFeignRemoteClient;
import element.io.mall.common.service.MemberFeignRemoteClient;
import element.io.mall.common.to.OauthLoginTo;
import element.io.mall.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
@RefreshScope
@Slf4j
@Controller
@RequestMapping("/oauth")
public class OauthController {

	@Resource
	private GiteeFeignRemoteClient giteeFeignRemoteClient;

	@Resource
	private MemberFeignRemoteClient memberFeignRemoteClient;

	@Value("${oauth.client_secret}")
	private String client_secret;

	@Value("${oauth.grant_type}")
	private String grant_type;

	@Value("${oauth.client_id}")
	private String client_id;

	@Value("${oauth.redirect_uri}")
	private String redirect_uri;


	//	 http://auth.gulimall.com/oauth/gitee/success
	@GetMapping("/gitee/token")
	public String callback(String code, HttpSession httpSession) throws IOException {
		/**
		 {"access_token":"dd3c839a68058666ec4e0b7ab0c0e93a","token_type":"bearer","expires_in":86400,
		 "refresh_token":"e722d970debf2cfdec210c38a10592bdbbb3218b609c742291f4781c2e97faf6",
		 "scope":"user_info projects pull_requests issues notes keys hook groups gists enterprises emails",
		 "created_at":1669197724}
		 */
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost("https://gitee.com/oauth/token");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("grant_type", grant_type));
		nameValuePairs.add(new BasicNameValuePair("code", code));
		nameValuePairs.add(new BasicNameValuePair("client_id", client_id));
		nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirect_uri));
		nameValuePairs.add(new BasicNameValuePair("client_secret", client_secret));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		CloseableHttpResponse httpResponse = httpClient.execute(post);
		String response = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
		JSONObject jsonObject = JSON.parseObject(response);
		String token = (String) jsonObject.get("access_token");
		log.info("获取到的token的值{}", token);
		String expires_in = jsonObject.get("expires_in").toString();
		String userInfo = giteeFeignRemoteClient.getUserInfo(token);
		OauthLoginTo to = new OauthLoginTo();
		to.setAccess_token(token);
		JSONObject parseObject = JSON.parseObject(userInfo);
		Long id = Long.valueOf(parseObject.get("id").toString());
		String name = (String) parseObject.get("name");
		to.setId(id);
		to.setName(name);
		to.setExpires_in(expires_in);
		log.info("获取到的用户信息{}", userInfo);
		R r = memberFeignRemoteClient.oauthLogin(to);
		// 获取到当前登陆用户的信息
		MemberEntity member = r.getData(new TypeReference<MemberEntity>() {
		});
		httpSession.setAttribute(SessionConstants.LOGIN_USER, member);
		log.info("member服务返回的执行结果{}", member);
		return "redirect:http://www.gulimall.com";
	}

	@ResponseBody
	@GetMapping("/callback")
	public String callback() {
		return "ok";
	}


}
