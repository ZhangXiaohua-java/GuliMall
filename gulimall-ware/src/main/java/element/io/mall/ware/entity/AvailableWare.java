package element.io.mall.ware.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
@Data
public class AvailableWare {

	private Long skuId;

	private Integer count;

	private List<Long> wareIds;
}
