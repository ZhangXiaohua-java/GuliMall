package element.io.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

	List<Long> batchFindGroupId(@Param("list") ArrayList<Long> attrIds);
	

}
