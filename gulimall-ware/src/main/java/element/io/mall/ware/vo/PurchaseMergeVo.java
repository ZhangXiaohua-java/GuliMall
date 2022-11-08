package element.io.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-7
 */
@Data
public class PurchaseMergeVo {

	private Long purchaseId;
	

	private List<Long> items;

}
