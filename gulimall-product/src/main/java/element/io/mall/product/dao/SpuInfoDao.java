package element.io.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

	SpuInfoEntity selectSpuInfoBySkuId(@Param("skuId") Long skuId);

}
