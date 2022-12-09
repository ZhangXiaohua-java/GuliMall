package element.io.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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


	List<Long> selectAvailableWares(@Param("skuId") Long skuId);

	int lockStock(@Param("skuId") Long skuId, @Param("count") Integer count, @Param("wareId") Long wareId);

	int unlock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
