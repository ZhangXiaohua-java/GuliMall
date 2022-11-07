package element.io.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuInfoVo {

	private String spuName;

	private String spuDescription;

	private Long catalogId;

	private Long brandId;

	private BigDecimal weight;

	private int publishStatus;
	// .
	private List<String> decript;

	private List<String> images;

	private Bounds bounds;

	private List<BaseAttrs> baseAttrs;

	private List<Skus> skus;

}