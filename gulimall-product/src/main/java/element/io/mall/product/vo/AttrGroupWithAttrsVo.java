package element.io.mall.product.vo;

import element.io.mall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-6
 */
@Data
public class AttrGroupWithAttrsVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分组id
	 */
	private Long attrGroupId;
	/**
	 * 组名
	 */
	private String attrGroupName;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 描述
	 */
	private String descript;
	/**
	 * 组图标
	 */
	private String icon;
	/**
	 * 所属分类id
	 */
	private Long catelogId;

	/**
	 * 该分组下的所有属性信息
	 */
	private List<AttrEntity> attrs;


}
