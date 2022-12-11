package element.io.mall.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SeckillSkuRelationTo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;
	/**
	 * 活动id
	 */
	private Long promotionId;
	/**
	 * 活动场次id
	 */
	private Long promotionSessionId;
	/**
	 * 商品id
	 */
	private Long skuId;
	/**
	 * 秒杀价格
	 */
	private BigDecimal seckillPrice;
	/**
	 * 秒杀总量
	 */
	private Integer seckillCount;
	/**
	 * 每人限购数量
	 */
	private Integer seckillLimit;
	/**
	 * 排序
	 */
	private Integer seckillSort;


	private SkuInfoTo skuInfo;

	private Long start;

	private Long end;

	private String token;

}