package element.io.search.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 张晓华
 * @date 2022-11-10
 */
@AllArgsConstructor
@Builder
@Data
public class User implements Serializable {


	private Long id;


	private Integer age;

	private String name;

	private String gender;

	private String address;
	
	private String email;

}


