package element.io.mall.common.ex;

/**
 * @author 张晓华
 * @date 2022-12-7
 */
public class UnlockException extends RuntimeException {

	public UnlockException(Object obj) {
		super(obj + "的详情单的库存解锁失败");
	}


}
