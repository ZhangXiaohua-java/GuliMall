package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.vo.AttrResponseVo;
import element.io.mall.product.vo.AttrVo;
import element.io.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface AttrService extends IService<AttrEntity> {

	PageUtils queryPage(Map<String, Object> params);

	PageUtils queryForPage(Map<String, Object> params);

	boolean saveAttr(AttrVo attrVo);


	AttrResponseVo getDetail(Long attrId);

	boolean updateInfo(AttrVo vo);

	List<AttrEntity> batchQueryAttrs(List<Long> attrIds);


	List<Long> findQuickShowAttrs(List<Long> skuAttrIds);

	List<SpuItemAttrGroupVo> getBasicAttrsWithCatalogIdAndSpuId(Long catalogId, Long spuId);

}

