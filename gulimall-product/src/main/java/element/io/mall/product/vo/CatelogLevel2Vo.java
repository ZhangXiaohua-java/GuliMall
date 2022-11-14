package element.io.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@Data
public class CatelogLevel2Vo implements Serializable {


	private String catalog1Id;

	private String id;

	private String name;

	private List<CatelogLevel3Vo> catalog3List;


	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class CatelogLevel3Vo implements Serializable {
		private String catalog2Id;

		private String id;

		private String name;

	}

}
