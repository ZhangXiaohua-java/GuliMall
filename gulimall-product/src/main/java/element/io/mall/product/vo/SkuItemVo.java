package element.io.mall.product.vo;

import element.io.mall.product.entity.SkuImagesEntity;
import element.io.mall.product.entity.SkuInfoEntity;
import element.io.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-20
 */
@Data
public class SkuItemVo {

	// sku的基本信息
	private SkuInfoEntity info;

	// sku的图片信息
	private List<SkuImagesEntity> images;

	// sku对应的spu的描述信息
	private SpuInfoDescEntity desc;

	// 销售属性信息
	private List<SkuItemSaleAttrVo> saleAttrVos;

	// 分组属性信息
	private List<SpuItemAttrGroupVo> baseAttrs;


}
