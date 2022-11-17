package element.io.search.vo;

import element.io.mall.common.to.SkuEsModelTo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-17
 */
@Data
public class SearchResultVo implements Serializable {

	private List<SkuEsModelTo> products; // v

	private Long pageNum;  // v

	private Long totalPages; // v

	private List<Attr> attrs;

	private List<Brand> brands;

	private List<Catalog> catalogs;

	@Data
	public static class Attr {

		private Long attrId;

		private String attrName;

		private List<String> attrValue;

	}

	@Data
	public static class Catalog {
		private Long catalogId;
		private String catalogName;
	}

	@Data
	public static class Brand {
		private Long brandId;

		private String brandName;

		private String brandImg;
	}


}
