package element.io.mall.common.ex;

/**
 * @author 张晓华
 * @date 2022-12-4
 */
public class NoStockException extends RuntimeException {


	public NoStockException(Long skuId) {
		super(skuId + "商品没有库存");
	}


	public NoStockException(String message) {
		super(message);
	}
}
