package element.io.mall.common.msg;

import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Data
public class StockLockedTo {

	private Long taskId;

	private List<ReleaseStockTaskTo> releaseTos;
	

}
