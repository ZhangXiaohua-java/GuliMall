package element.io.mall.cart.vo;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Data
public class UserInfo {

	private Long userId;

	private String key;

	private boolean isNew;

	private boolean isDelete = false;

}
