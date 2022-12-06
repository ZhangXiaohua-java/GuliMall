package element.io.mq.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 张晓华
 * @date 2022-11-28
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfo implements Serializable {

	private String name;

	private Integer id;

	private String gender;

}
