package element.io.mall.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
@Data
public class RegisterVo {

	@NotBlank(message = "用户名不可为空")
	@Size(min = 6, max = 18)
	private String username;

	@NotBlank(message = "手机号必须提交")
	@Pattern(regexp = "^1(3|5|7|8|9)\\d{9}$", message = "手机号必须符合格式")
	private String phone;

	@NotBlank(message = "密码不能为空")
	@Pattern(regexp = "^[a-z]+(\\w+){7}$", message = "密码只能以字母开头并且长度必须大于八位")
	private String password;

	@Pattern(regexp = "\\d{6}", message = "验证码只能是六位数字")
	@NotBlank(message = "验证码不能为空")
	private String verifyCode;

}
