package element.io.mall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemSaleAttrVo {

	private Long attrId;

	private String attrName;

	private List<AttrWithSkuIdsVo> valIds;

}