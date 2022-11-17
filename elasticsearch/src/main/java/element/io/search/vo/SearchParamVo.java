package element.io.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-17
 */
@Data
public class SearchParamVo {

	/**
	 * catalog3Id=225
	 * keyword=iPhone
	 */

	private Long catalog3Id;

	// 对应页面的keyword
	private String keyword;

	private List<Long> brandId;

	private Integer hasStock;

	private List<String> attrs;


	// 排序信息

	private String skuPrice;

	private String saleCount;

	private String hotScore;

	private String sort;

	private Integer pageNum;

	private Integer pageSize;


}
