package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SkuInfoDao;
import element.io.mall.product.dao.SkuSaleAttrValueDao;
import element.io.mall.product.entity.SkuImagesEntity;
import element.io.mall.product.entity.SkuInfoEntity;
import element.io.mall.product.entity.SpuInfoDescEntity;
import element.io.mall.product.service.AttrService;
import element.io.mall.product.service.SkuImagesService;
import element.io.mall.product.service.SkuInfoService;
import element.io.mall.product.service.SpuInfoDescService;
import element.io.mall.product.vo.SkuItemSaleAttrVo;
import element.io.mall.product.vo.SkuItemVo;
import element.io.mall.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


	@Resource
	private SkuImagesService skuImagesService;

	@Resource
	private SpuInfoDescService spuInfoDescService;

	@Resource
	private AttrService attrService;


	@Resource
	private SkuSaleAttrValueDao skuSaleAttrValueDao;

	@Resource(name = "productThreadPool")
	private ThreadPoolExecutor threadPoolExecutor;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : null;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : null;
		Long brandId = Objects.nonNull(params.get("brandId")) && !params.get("brandId").equals("0") ? Long.valueOf(params.get("brandId").toString()) : null;
		Long catelogId = Objects.nonNull(params.get("catelogId")) && !params.get("catelogId").equals("0") ? Long.valueOf(params.get("catelogId").toString()) : null;
		BigDecimal min = Objects.nonNull(params.get("min")) ? new BigDecimal(params.get("min").toString()) : null;
		BigDecimal max = Objects.nonNull(params.get("max")) && !params.get("max").equals("0") ? new BigDecimal(params.get("max").toString()) : null;
		Page<SkuInfoEntity> skuInfoEntityPage = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Objects.nonNull(catelogId), SkuInfoEntity::getCatalogId, catelogId)
				.and(Objects.nonNull(brandId), c -> c.eq(SkuInfoEntity::getBrandId, brandId))
				.and(Objects.nonNull(min), con -> con.ge(SkuInfoEntity::getPrice, min))
				.and(Objects.nonNull(max), cond -> cond.le(SkuInfoEntity::getPrice, max))
				.and(StringUtils.hasText(key), condition -> condition.like(SkuInfoEntity::getSkuName, key).eq(SkuInfoEntity::getSkuId, key));
		this.baseMapper.selectPage(skuInfoEntityPage, queryWrapper);
		return new PageUtils(skuInfoEntityPage.getRecords(), Long.valueOf(skuInfoEntityPage.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public List<SkuInfoEntity> querySkusWithSpuId(Long spuId) {
		LambdaQueryWrapper<SkuInfoEntity> skuInfoEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		skuInfoEntityLambdaQueryWrapper.eq(SkuInfoEntity::getSpuId, spuId);
		return this.baseMapper.selectList(skuInfoEntityLambdaQueryWrapper);
	}

	@Override
	public SkuItemVo getSkuDetailInfo(Long skuId) throws ExecutionException, InterruptedException {
		SkuItemVo vo = new SkuItemVo();
		// 1 ??????sku???????????????,?????????????????????,????????????????????????????????????????????????????????????
		CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
			SkuInfoEntity skuInfo = this.getById(skuId);
			vo.setInfo(skuInfo);
			return skuInfo;
		}, threadPoolExecutor);

		// 2 ??????sku???????????????,?????????????????????????????????,???????????????????????????????????????
		CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
			List<SkuImagesEntity> images = skuImagesService.getSkuImagesBySkuId(skuId);
			vo.setImages(images);
		}, threadPoolExecutor);

		// 3 ?????????sku?????????spu?????????????????????,???????????????????????????????????????????????????
		CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((result) -> {
			SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getSpuDescBySpuId(result.getSpuId());
			vo.setDesc(spuInfoDesc);
		}, threadPoolExecutor);

		// 4 ?????????spu???????????????????????????,??????????????????
		CompletableFuture<Void> basicAttrFuture = infoFuture.thenAcceptAsync((result) -> {
			List<SpuItemAttrGroupVo> basicAttrs = attrService.getBasicAttrsWithCatalogIdAndSpuId(result.getCatalogId(), result.getSpuId());
			vo.setBaseAttrs(basicAttrs);
		}, threadPoolExecutor);

		// 5 ?????????spu???????????????????????????
		CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((result) -> {
			List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueDao.selectAllSaleAttrsBySpuId(result.getSpuId());
			vo.setSaleAttrVos(saleAttrVos);
		}, threadPoolExecutor);

		// ?????????????????????????????????????????????????????????,??????????????????????????????????????????????????????,
		// get??????????????????,???join??????,????????????????????????????????????,????????????????????????????????????
		CompletableFuture.allOf(imageFuture, descFuture, basicAttrFuture, saleAttrFuture).get();
		return vo;
	}


	/**
	 * SELECT
	 * pag.attr_group_name,
	 * pa.attr_name,
	 * pda.attr_value
	 * FROM
	 * pms_attr_group AS pag
	 * LEFT JOIN pms_attr_attrgroup_relation AS par ON pag.attr_group_id = par.attr_group_id
	 * LEFT JOIN pms_attr AS pa ON pa.attr_id = par.attr_id
	 * LEFT JOIN pms_product_attr_value AS pda ON pda.attr_id = pa.attr_id
	 * WHERE
	 * pag.catelog_id = 225
	 * AND pda.spu_id = 17
	 */

	@Override
	public List<SkuInfoEntity> batchQuerySkuPrice(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.select(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice)
				.in(!CollectionUtils.isEmpty(ids), SkuInfoEntity::getSkuId, ids);
		return this.list(wrapper);
	}


	@Override
	public Map<Long, SkuInfoEntity> batchQuerySkuInfo(List<Long> skuIds) {
		LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(SkuInfoEntity::getSkuId, skuIds);
		List<SkuInfoEntity> list = this.list(wrapper);
		Map<Long, List<SkuInfoEntity>> collect = list.stream().collect(Collectors.groupingBy(e -> e.getSkuId()));
		Map<Long, SkuInfoEntity> map = new HashMap();
		collect.forEach((k, v) -> {
			map.put(k, v.get(0));
		});
		return map;
	}


}