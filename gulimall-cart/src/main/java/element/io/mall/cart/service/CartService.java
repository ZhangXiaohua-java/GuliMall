package element.io.mall.cart.service;

import element.io.mall.cart.vo.CartEntity;
import element.io.mall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 张晓华
 * @date 2022-11-27
 */
public interface CartService {

	CartItem addItem(Long skuId, Integer count) throws ExecutionException, InterruptedException;

	CartItem queryRecentAddedItem(Long skuId);

	CartEntity queryUserCartInfo();

	List<CartItem> getItemsByKey(String key);

	boolean deleteItem(Long skuId);

	void updateItemCheckStatus(Long skuId, Integer status);

	void updateItemCount(Long skuId, Integer count);

	List<CartItem> getUserCheckedItems();
	
}
