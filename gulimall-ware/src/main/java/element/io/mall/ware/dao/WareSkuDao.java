package element.io.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

	Long selectStock(@Param("skuId") Long skuId);

}
