package element.io.mall.common.msg;

import lombok.Data;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
@Data
public class ReleaseStockTaskTo {

	public static final Integer LOCKED = 1;

	public static final Integer UNLOCKED = 1;
	
	public static final Integer VERIFIED = 1;
	/**
	 * id
	 */
	private Long id;
	/**
	 * sku_id
	 */
	private Long skuId;
	/* 购买个数
	 */
	private Integer skuNum;
	/**
	 * 工作单id
	 */
	private Long taskId;
	/**
	 * 仓库id
	 */
	private Long wareId;
	/**
	 * 1-已锁定  2-已解锁  3-扣减
	 */
	private Integer lockStatus;

}
