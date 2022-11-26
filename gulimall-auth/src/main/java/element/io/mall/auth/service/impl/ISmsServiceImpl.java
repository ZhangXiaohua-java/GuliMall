package element.io.mall.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import element.io.mall.auth.service.ISmsService;
import element.io.mall.common.service.SmsRemoteClient;
import element.io.mall.common.to.SmsDto;
import element.io.mall.common.util.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;

import static element.io.mall.common.enumerations.AuthConstant.CODE_PREFIX;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
@Slf4j
@Service
public class ISmsServiceImpl implements ISmsService {

	@Value("${sms.smsSignId}")
	private String smsSignId;


	@Value("${sms.templateId}")
	private String templateId;


	@Resource
	private SmsRemoteClient smsRemoteClient;

	@Resource
	private RedisTemplate redisTemplate;

	// param "**code**:52100,**minute**:15"
	@Override
	public boolean sendCode(String phone, String ip) {
		ValueOperations ops = redisTemplate.opsForValue();
		Long times = ops.increment(ip);
		if (times >= 5) {
			return false;
		}
		Object tmp = ops.get(CODE_PREFIX + phone);
		if (Objects.nonNull(tmp) && StringUtils.hasText(tmp.toString())) {
			String[] res = tmp.toString().split("_");
			if (System.currentTimeMillis() - Long.valueOf(res[1]) <= 60000) {
				return false;
			}
		}
		String param = "**code**:000000,**minute**:15";
		String code = CodeUtils.generateRandomCode(6);
		param = param.replace("000000", code);
		log.info("接收验证码的手机号{}", phone);
		String result = smsRemoteClient.sendCode(phone, param, smsSignId, templateId).toString();
		SmsDto res = JSON.parseObject(result, new TypeReference<SmsDto>() {
		});
		log.info("远程调用的结果{}", res);
		code += ("_" + System.currentTimeMillis());
		if (res.getCode().equals(0)) {
			ops.set(CODE_PREFIX + phone, code, Duration.ofMinutes(15));
			return true;
		} else {
			return false;
		}
	}


	@Override
	public boolean verifyCode(String phone, String verifyCode) {
		ValueOperations ops = redisTemplate.opsForValue();
		Object res = ops.get(CODE_PREFIX + phone);
		if (Objects.isNull(res) || StringUtils.isEmpty(res)) {
			return false;
		}
		String code = res.toString().split("_")[0];
		if (code.equals(verifyCode)) {
			redisTemplate.delete(CODE_PREFIX + phone);
			return true;
		}
		return false;

	}


}
