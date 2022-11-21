package element.io.mall.auth.service;

/**
 * @author 张晓华
 * @date 2022-11-21
 */
public interface ISmsService {

	public boolean sendCode(String phone, String ip);


}
