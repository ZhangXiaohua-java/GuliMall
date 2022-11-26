package element.io.mall.common.to;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-11-23
 */
@Data
public class OauthLoginTo {

	private String access_token;

	private Long id;

	private String name;

	private String expires_in;


}
