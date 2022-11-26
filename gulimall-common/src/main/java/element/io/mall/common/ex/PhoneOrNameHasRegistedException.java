package element.io.mall.common.ex;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
public class PhoneOrNameHasRegistedException extends RuntimeException {

	public PhoneOrNameHasRegistedException() {
		super("用户名或者手机号已经被注册了");
	}
	

}
