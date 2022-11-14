package element.io.mall.ware.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-7
 */
@Data
public class PurchaseVo implements Serializable {

	private Long purchaseId;

	private List<Item> items;

	
}
