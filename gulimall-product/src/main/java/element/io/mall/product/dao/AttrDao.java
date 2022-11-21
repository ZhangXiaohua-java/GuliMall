package element.io.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

	List<SpuItemAttrGroupVo> selectBasicAttrsOfSpu(@Param("catalogId") Long catalogId, @Param("spuId") Long spuId);
}
