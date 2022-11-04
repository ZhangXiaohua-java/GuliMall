package element.io.mall.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 张晓华
 * @date 2022-11-3
 */
@Data
public class AttrGroupRelationVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long attrId;

	private Long attrGroupId;

}
