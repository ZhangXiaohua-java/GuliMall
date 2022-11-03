package element.io.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import element.io.mall.product.entity.AttrGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

	List<AttrGroupEntity> batchFindNames(@Param("list") List<Long> groupIds);


}
