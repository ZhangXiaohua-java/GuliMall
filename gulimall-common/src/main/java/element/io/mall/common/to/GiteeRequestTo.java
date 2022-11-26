package element.io.mall.common.to;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
@Data
public class GiteeRequestTo {

	/**
	 * grant_type=authorization_code
	 * &code={code}
	 * client_id={client_id}
	 * redirect_uri={redirect_uri}
	 * client_secret={client_secret}
	 */

	private String grant_type;


	private String code;


	private String client_id;


	private String redirect_uri;


	private String client_secret;


}
