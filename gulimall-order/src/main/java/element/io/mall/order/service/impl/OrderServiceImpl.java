package element.io.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.domain.MemberEntity;
import element.io.mall.common.ex.NoStockException;
import element.io.mall.common.feign.CartRemoteFeignClient;
import element.io.mall.common.service.MemberFeignRemoteClient;
import element.io.mall.common.service.ProductFeignRemoteClient;
import element.io.mall.common.service.WareFeignRemoteClient;
import element.io.mall.common.to.*;
import element.io.mall.common.util.DataUtil;
import element.io.mall.common.util.FileUtils;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.order.component.UserInfoContext;
import element.io.mall.order.dao.OrderDao;
import element.io.mall.order.entity.OrderEntity;
import element.io.mall.order.entity.OrderItemEntity;
import element.io.mall.order.service.OrderItemService;
import element.io.mall.order.service.OrderService;
import element.io.mall.order.vo.CheckResponseVo;
import element.io.mall.order.vo.OrderRequestVo;
import element.io.mall.order.vo.OrderVo;
import element.io.mall.order.vo.SubmitOrderResponseVo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static element.io.mall.common.enumerations.OrderConstants.KEY_TIME;
import static element.io.mall.common.enumerations.OrderConstants.USER_ORDER_TOKEN_PREFIX;
import static element.io.mall.common.enumerations.OrderStatusEnum.CREATE_NEW;


@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

	@Resource
	private MemberFeignRemoteClient memberFeignRemoteClient;

	@Resource
	private CartRemoteFeignClient cartRemoteFeignClient;


	@Resource
	private WareFeignRemoteClient wareFeignRemoteClient;

	@Resource
	private ProductFeignRemoteClient productFeignRemoteClient;

	@Resource
	private ThreadPoolExecutor threadPoolExecutor;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Resource
	private OrderItemService orderItemService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public CheckResponseVo checkToPay() throws ExecutionException, InterruptedException {
		CheckResponseVo vo = new CheckResponseVo();
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		// 1 查询用户的收货地址信息
		MemberEntity member = UserInfoContext.currentContext().get();
		CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
			// 异步线程无法获取到当前的主线程绑定的请求上下文对象,所以需要手动设置
			RequestContextHolder.setRequestAttributes(requestAttributes, true);
			List<MemberReceiveAddressTo> receiveAddress = memberFeignRemoteClient.receiveAddress(member.getId());
			vo.setReceiveAddressTos(receiveAddress);
		}, threadPoolExecutor);

		// 2 查询用户购物车中所有被选中的信息
		CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
			RequestContextHolder.setRequestAttributes(requestAttributes, true);
			List<CartItemTo> cartItemTos = DataUtil.typeConvert(cartRemoteFeignClient.checkedItems().get("data"), new TypeReference<List<CartItemTo>>() {
			});
			vo.setItems(cartItemTos);
		}, threadPoolExecutor).thenRunAsync(() -> {
			List<Long> ids = vo.getItems().stream().map(CartItemTo::getSkuId).collect(Collectors.toList());
			log.info("ids {}", ids);
			List<WareSkuTo> tos = wareFeignRemoteClient.goodsStock(ids);
			log.info("tos {}", tos);
			if (CollectionUtils.isEmpty(tos)) {
				HashMap<Long, Boolean> map = new HashMap<>();
				for (Long id : ids) {
					map.put(id, false);
				}
				vo.setStockStatus(map);
				return;
			}
			Map<Long, Boolean> map = tos.stream().map(e -> {
				e.setHasStock(false);
				vo.getItems().stream()
						.filter(ele -> ele.getSkuId().equals(e.getSkuId()))
						.findFirst().ifPresent(element -> {
							if (e.getStock() != null && e.getStock() - element.getCount() > 0) {
								e.setHasStock(true);
							} else {
								e.setHasStock(false);
							}
						});
				return e;
			}).collect(Collectors.toMap(a -> a.getSkuId(), a -> a.isHasStock(), (k1, k2) -> k1));
			vo.setStockStatus(map);
		}, threadPoolExecutor);
		CompletableFuture<Void> tokenFuture = CompletableFuture.runAsync(() -> {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			redisTemplate.opsForValue().set(USER_ORDER_TOKEN_PREFIX + member.getId(), uuid, Duration.ofMinutes(KEY_TIME));
			vo.setOrderToken(uuid);
		});
		vo.setIntegration(UserInfoContext.currentContext().get().getIntegration());
		CompletableFuture.allOf(addressFuture, cartFuture, tokenFuture).get();
		log.info("查询到的结果: {}", vo);
		return vo;
	}


	@GlobalTransactional(rollbackFor = {Throwable.class})
	@Override
	public SubmitOrderResponseVo createOrder(OrderRequestVo vo) throws IOException {
		SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("lua/VerifyToken.lua");
		assert inputStream != null;
		String scriptStr = FileUtils.readFileToString(inputStream);
		RedisScript<Boolean> script = RedisScript.of(scriptStr, Boolean.class);
		Object o = redisTemplate.opsForValue().get(USER_ORDER_TOKEN_PREFIX + UserInfoContext.currentContext().get().getId());
		if (Objects.nonNull(o)) {
			Boolean result = redisTemplate.execute(script, Collections.singletonList(USER_ORDER_TOKEN_PREFIX + UserInfoContext.currentContext().get().getId()), o.toString());
			if (result) {
				log.info("验证通过");
				OrderVo orderVo = buildOrder(vo);
				log.info("计算出的orderVo信息{}", orderVo);
				this.baseMapper.insert(orderVo.getOrder());
				orderItemService.saveBatch(orderVo.getItems());
				//	 锁定库存
				List<StockLockTo> tos = orderVo.getItems().stream().map(i -> {
					StockLockTo to = new StockLockTo();
					to.setSkuId(i.getSkuId());
					to.setLockCount(i.getSkuQuantity());
					return to;
				}).collect(Collectors.toList());
				R r = wareFeignRemoteClient.lockStock(tos);
				int num = 10 / 0;
				if (0 == (int) r.get("code")) {
					responseVo.setCode(0);
				} else {
					throw new NoStockException("商品库存不足");
				}
			} else {
				log.info("验证失败");
				responseVo.setCode(-1);
			}
		}
		return responseVo;
	}

	private OrderVo buildOrder(OrderRequestVo vo) {
		MemberEntity member = UserInfoContext.currentContext().get();
		OrderVo orderVo = new OrderVo();
		OrderEntity order = new OrderEntity();
		orderVo.setOrder(order);
		// 订单相关的基础信息
		String orderSn = IdWorker.getTimeId();
		order.setOrderSn(orderSn);
		order.setCreateTime(new Date());
		order.setModifyTime(new Date());
		order.setStatus(CREATE_NEW.getCode());
		order.setDeleteStatus(0);
		order.setMemberId(member.getId());
		order.setMemberUsername(StringUtils.hasText(member.getUsername()) ? member.getUsername() : member.getNickname());
		// 15天后自动收货
		order.setAutoConfirmDay(15);
		// 收货地址信息
		CourierTo courierTo = wareFeignRemoteClient.countFee(vo.getAddrId());
		// 运费
		order.setFreightAmount(courierTo.getPrice());
		MemberReceiveAddressTo address = courierTo.getAddress();
		order.setReceiverName(address.getName());
		order.setReceiverProvince(address.getProvince());
		order.setReceiverCity(address.getCity());
		order.setReceiverRegion(address.getRegion());
		order.setReceiverDetailAddress(address.getDetailAddress());
		order.setReceiverPostCode(address.getPostCode());
		order.setReceiverPhone(address.getPhone());
		// 订单项信息
		List<OrderItemEntity> orderItems = buildOrderItems(vo, orderSn);
		orderVo.setItems(orderItems);
		// 计算应付价格以及部分的优惠信息
		computePrice(order, orderItems);
		return orderVo;
	}

	private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
		BigDecimal totalPay = new BigDecimal("0");
		Integer integration = 0;
		Integer growth = 0;
		BigDecimal save = new BigDecimal("0");
		for (OrderItemEntity item : orderItems) {
			totalPay = totalPay.add(item.getRealAmount());
			integration += item.getGiftIntegration();
			growth += item.getGiftGrowth();
			save = save.add(item.getIntegrationAmount());
		}
		order.setPayAmount(totalPay);
		order.setIntegration(integration);
		order.setGrowth(growth);
		order.setIntegrationAmount(save);
	}

	private List<OrderItemEntity> buildOrderItems(OrderRequestVo vo, String orderSn) {
		R r = cartRemoteFeignClient.checkedItems();
		List<CartItemTo> cartItemTos = DataUtil.typeConvert(r.get("data"), new TypeReference<List<CartItemTo>>() {
		});
		return cartItemTos.stream()
				.map(e -> {
					return buildOrderItem(e, orderSn);
				}).collect(Collectors.toList());
	}

	private OrderItemEntity buildOrderItem(CartItemTo to, String orderSn) {
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		SpuInfoTo spu = productFeignRemoteClient.querySpuInfoBySkuId(to.getSkuId());
		orderItemEntity.setOrderSn(orderSn);
		orderItemEntity.setSpuId(spu.getId());
		orderItemEntity.setSpuPic(spu.getSpuDescription());
		orderItemEntity.setCategoryId(spu.getCatalogId());
		orderItemEntity.setSpuBrand(spu.getBrandId() + "");
		orderItemEntity.setSpuName(spu.getSpuName());
		orderItemEntity.setSkuId(to.getSkuId());
		orderItemEntity.setSkuName(to.getTitle());
		orderItemEntity.setSkuPic(to.getImage());
		orderItemEntity.setSkuPrice(to.getPrice());
		orderItemEntity.setSkuQuantity(to.getCount());
		orderItemEntity.setSkuAttrsVals(JSON.toJSONString(to.getSaleAttrs()));
		ThreadLocalRandom current = ThreadLocalRandom.current();
		orderItemEntity.setPromotionAmount(new BigDecimal(current.nextInt(10)));
		orderItemEntity.setCouponAmount(new BigDecimal(current.nextInt(10)));
		orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
		BigDecimal cou = to.getPrice().multiply(new BigDecimal(to.getCount())).subtract(orderItemEntity.getPromotionAmount())
				.subtract(orderItemEntity.getIntegrationAmount());
		orderItemEntity.setRealAmount(cou);
		orderItemEntity.setGiftIntegration(orderItemEntity.getRealAmount().intValue());
		orderItemEntity.setGiftGrowth(orderItemEntity.getRealAmount().intValue() * 10);
		return orderItemEntity;
	}


}