package element.io.mall.product.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 张晓华
 * @date 2022-11-2
 */
@Data
public class AttrResponseVo extends AttrVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String catelogName;

	private String groupName;

	private Long[] categoryPath;

}
