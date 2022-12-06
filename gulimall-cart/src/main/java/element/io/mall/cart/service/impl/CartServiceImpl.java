package element.io.mall.cart.service.impl;

import com.alibaba.fastjson.TypeReference;
import element.io.mall.cart.component.UserInfoContext;
import element.io.mall.cart.service.CartService;
import element.io.mall.cart.vo.CartEntity;
import element.io.mall.cart.vo.CartItem;
import element.io.mall.cart.vo.UserInfo;
import element.io.mall.common.enumerations.CartConstants;
import element.io.mall.common.service.ProductFeignRemoteClient;
import element.io.mall.common.to.SkuInfoEntityTo;
import element.io.mall.common.to.SkuInfoTo;
import element.io.mall.common.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author 张晓华
 * @date 2022-11-27
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private ProductFeignRemoteClient productFeignRemoteClient;

	@Resource
	private ThreadPoolExecutor executor;

	@Override
	public CartItem addItem(Long skuId, Integer count) throws ExecutionException, InterruptedException {
		BoundHashOperations<String, String, Object> ops = getOps();
		Object storageData = ops.get(String.valueOf(skuId));
		log.info("查询到的redis中的信息{}", storageData);
		if (Objects.isNull(storageData)) {
			//	 查询商品信息
			CartItem item = new CartItem();
			CompletableFuture<Void> basicInfoFuture = CompletableFuture.runAsync(() -> {
				SkuInfoTo data = DataUtil.typeConvert(productFeignRemoteClient.info(skuId).get("skuInfo"), new TypeReference<SkuInfoTo>() {
				});
				log.info("获取到的结果{}", data);
				item.setChecked(true);
				item.setCount(count);
				item.setImage(data.getSkuDefaultImg());
				item.setPrice(data.getPrice());
				item.setTitle(data.getSkuTitle());
				item.setSkuId(skuId);
			}, executor);
			//	 查询商品的属性信息
			CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
				List<String> attrs = productFeignRemoteClient.saleAttr(skuId);
				log.info("获取到的属性信息{}", attrs);
				item.setSaleAttrs(attrs);
			}, executor);
			CompletableFuture.allOf(basicInfoFuture, saleAttrFuture).get();
			//	 将商品信息存入redis中
			ops.put(skuId.toString(), item);
			//	 返回数据
			return item;
		}
		//	 修改数据
		CartItem item = (CartItem) storageData;
		item.setCount(item.getCount() + count);
		ops.put(skuId.toString(), item);
		//	 返回
		return item;
	}

	private BoundHashOperations<String, String, Object> getOps() {
		UserInfo info = UserInfoContext.currentContext().get();
		String key = CartConstants.CART_PREFIX;
		if (Objects.nonNull(info.getUserId())) {
			key += info.getUserId();
		} else {
			key += info.getKey();
		}
		return redisTemplate.boundHashOps(key);
	}


	@Override
	public CartItem queryRecentAddedItem(Long skuId) {
		BoundHashOperations<String, String, Object> ops = getOps();
		return (CartItem) ops.get(String.valueOf(skuId));
	}


	@Override
	public CartEntity queryUserCartInfo() {
		UserInfo info = UserInfoContext.currentContext().get();
		CartEntity cart = new CartEntity();
		// 需要区分用户是否登陆,未登录用户的处理
		if (Objects.isNull(info.getUserId())) {
			List<CartItem> items = getItemsByKey(info.getKey());
			cart.setItems(items);
			return cart;
		}
		//	 登录用户需要判断该用户是否有临时购物车的数据需要处理
		List<CartItem> items = getItemsByKey(info.getKey());
		if (CollectionUtils.isEmpty(items)) {
			cart.setItems(getItemsByKey(info.getUserId().toString()));
			return cart;
		}
		BoundHashOperations<String, String, Object> ops = getOps();
		Map<String, CartItem> itemMap = items.stream().collect(Collectors.toMap(item -> item.getSkuId().toString(), cartItem -> cartItem));
		List<CartItem> list = getItemsByKey(info.getUserId().toString());
		Map<String, CartItem> existedMap = list.stream()
				.collect(Collectors.toMap((e) -> e.getSkuId().toString(), (l) -> l));
		HashSet<String> sub = new HashSet<>();
		sub.addAll(itemMap.keySet());
		sub.removeAll(existedMap.keySet());
		// 此时sub中的值就是用户账户对应的购物车中没有商品信息,使用集合的交差运算来过滤数据
		HashMap<String, CartItem> add = new HashMap<>();
		for (String key : sub) {
			add.put(key, itemMap.get(key));
		}
		sub = new HashSet<>();
		sub.addAll(itemMap.keySet());
		sub.retainAll(existedMap.keySet());
		for (String key : sub) {
			CartItem loginItem = existedMap.get(key);
			CartItem anoItem = itemMap.get(key);
			loginItem.setCount(loginItem.getCount() + anoItem.getCount());
			add.put(key, loginItem);
		}
		redisTemplate.delete(CartConstants.CART_PREFIX + info.getKey());
		ops.putAll(add);
		// -----------
		cart.setItems(getItemsByKey(info.getUserId().toString()));
		info.setDelete(true);
		return cart;
	}

	@Override
	public List<CartItem> getItemsByKey(String key) {
		BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(CartConstants.CART_PREFIX + key);
		Map<String, Object> entries = ops.entries();
		return entries.values().stream()
				.map(item -> {
					return (CartItem) item;
				}).collect(Collectors.toList());
	}

	@Override
	public boolean deleteItem(Long skuId) {
		BoundHashOperations<String, String, Object> ops = getOps();
		Long delete = ops.delete(skuId.toString());
		return delete == 1;
	}

	@Override
	public void updateItemCheckStatus(Long skuId, Integer status) {
		BoundHashOperations<String, String, Object> ops = getOps();
		CartItem data = (CartItem) ops.get(skuId.toString());
		data.setChecked(status == 1);
		ops.put(skuId.toString(), data);
	}

	@Override
	public void updateItemCount(Long skuId, Integer count) {
		BoundHashOperations<String, String, Object> ops = getOps();
		CartItem data = (CartItem) ops.get(skuId.toString());
		data.setCount(count);
		ops.put(skuId.toString(), data);
	}

	@Override
	public List<CartItem> getUserCheckedItems() {
		UserInfo info = UserInfoContext.CONTEXT.get();
		List<CartItem> list = getItemsByKey(info.getUserId() + "");
		// 查询这些商品的最新价格
		List<Long> skuIds = list.stream().filter(e -> e.getChecked())
				.map(i -> i.getSkuId()).collect(Collectors.toList());
		List<SkuInfoEntityTo> skuInfoEntityTos = productFeignRemoteClient.batchQuerySkuPrice(skuIds);
		list = list.stream().filter(e -> e.getChecked())
				.map(item -> {
					item.setPrice(new BigDecimal("99999999"));
					// 更新商品的价格为最新的价格,假如因为程序错误没有查询到数据,就给商品设置一个离谱的价格不让下单
					skuInfoEntityTos.stream().filter(ele -> ele.getSkuId().equals(item.getSkuId()))
							.findAny().ifPresent(r -> item.setPrice(r.getPrice()));
					return item;
				}).collect(Collectors.toList());
		return list;
	}

}
