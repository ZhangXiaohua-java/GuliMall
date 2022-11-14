package element.io.mall.ware.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 张晓华
 * @date 2022-11-8
 */
@Data
public class Item implements Serializable {

	private Long itemId;

	private Integer status;

	private String reason;
}
