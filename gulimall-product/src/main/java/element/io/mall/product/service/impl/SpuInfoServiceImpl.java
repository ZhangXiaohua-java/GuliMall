package element.io.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.service.CouponRemoteClient;
import element.io.mall.common.service.SearchFeignRemoteClient;
import element.io.mall.common.service.WareFeignRemoteClient;
import element.io.mall.common.to.SkuEsModelTo;
import element.io.mall.common.to.SkuReductionTo;
import element.io.mall.common.to.SkuStockVo;
import element.io.mall.common.to.SpuBoundTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.dao.SpuInfoDao;
import element.io.mall.product.entity.*;
import element.io.mall.product.service.*;
import element.io.mall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@SuppressWarnings({"all"})
@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Resource
	private SpuInfoDescService spuInfoDescService;

	@Resource
	SpuImagesService spuImagesService;

	@Resource
	private ProductAttrValueService productAttrValueService;

	@Resource
	private AttrService attrService;

	@Resource
	private SkuInfoService skuInfoService;

	@Resource
	private SkuImagesService skuImagesService;


	@Resource
	private SkuSaleAttrValueService skuSaleAttrValueService;


	@Resource
	private CouponRemoteClient couponRemoteClient;

	@Resource
	private BrandService brandService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private WareFeignRemoteClient wareFeignRemoteClient;

	@Resource
	private SearchFeignRemoteClient searchFeignRemoteClient;


	/**
	 * status=&key=&brandId=0&catelogId=0&page=1&limit=10
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		Integer pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : null;
		Integer pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : null;
		Integer status = Objects.nonNull(params.get("status")) ? params.get("status").toString().equals("") ? null : Integer.parseInt(params.get("status").toString()) : null;
		Long brandId = Objects.nonNull(params.get("brandId")) && !params.get("brandId").equals("0") ? Long.valueOf(params.get("brandId").toString()) : null;
		Long catelogId = Objects.nonNull(params.get("catelogId")) && !params.get("catelogId").equals("0") ? Long.valueOf(params.get("catelogId").toString()) : null;
		LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Objects.nonNull(status), SpuInfoEntity::getPublishStatus, status)
				.and(Objects.nonNull(brandId), c -> c.eq(SpuInfoEntity::getBrandId, brandId))
				.and(Objects.nonNull(catelogId), condition -> condition.eq(SpuInfoEntity::getCatalogId, catelogId))
				.and(StringUtils.hasText(key), con -> con.like(SpuInfoEntity::getSpuName, key)
						.or().eq(SpuInfoEntity::getId, key).or());

		Page<SpuInfoEntity> page = new Page<>(pageNum, pageSize);
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public boolean saveSpuInfo(SpuInfoVo spuInfoVo) {
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
		// 创建时间,修改时间通通设置为当前时间
		spuInfoEntity.setCreateTime(new Date());
		spuInfoEntity.setUpdateTime(new Date());
		// 保存spu的基本信息,防止事务失效,此处通过使用Spring的代理对象来调用方法
		this.saveBasicInfo(spuInfoEntity);
		// 保存spu的描述信息
		SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
		spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
		spuInfoDescEntity.setDecript(String.join(",", spuInfoVo.getDecript()));
		spuInfoDescService.saveDesc(spuInfoDescEntity);
		// 远程服务调用
		Bounds bounds = spuInfoVo.getBounds();
		SpuBoundTo bound = new SpuBoundTo();
		bound.setBuyBounds(bounds.getBuyBounds());
		bound.setGrowBounds(bounds.getGrowBounds());
		bound.setSpuId(spuInfoEntity.getId());
		R r = couponRemoteClient.saveBoundInfo(bound);
		// 处理图片信息
		List<String> images = spuInfoVo.getImages();
		spuImagesService.saveImages(spuInfoEntity.getId(), images);
		// 处理spu的基本属性信息
		List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
		List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
		List<AttrEntity> attrEntities = attrService.batchQueryAttrs(attrIds);
		// 收集基本属性信息
		List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
			ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
			productAttrValueEntity.setSpuId(spuInfoEntity.getId());
			productAttrValueEntity.setAttrId(attr.getAttrId());
			// 属性名:需要查数据库,避免循环查库
			attrEntities.stream().filter(item -> item.getAttrId().equals(attr.getAttrId()))
					.findFirst()
					.ifPresent(item -> productAttrValueEntity.setAttrName(item.getAttrName()));
			productAttrValueEntity.setQuickShow(attr.getShowDesc());
			productAttrValueEntity.setAttrValue(attr.getAttrValues());
			return productAttrValueEntity;
		}).collect(Collectors.toList());
		productAttrValueService.batchSaveAttrs(productAttrValueEntities);
		List<Skus> skus = spuInfoVo.getSkus();
		List<SkuInfoEntity> skuInfoEntities = skus.stream().map(sk -> {
			SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
			BeanUtils.copyProperties(sk, skuInfoEntity);
			skuInfoEntity.setSpuId(spuInfoEntity.getId());
			skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
			skuInfoEntity.setBrandId(spuInfoVo.getBrandId());
			skuInfoEntity.setSaleCount(0L);
			sk.getImages().stream().filter(e -> e.getDefaultImg() == 1)
					.findFirst()
					.ifPresent(item -> skuInfoEntity.setSkuDefaultImg(item.getImgUrl()));
			// 保存sku的信息,因为下面的图片也要使用到sku的id,所以...
			skuInfoService.save(skuInfoEntity);
			// 收集sku的图片信息
			List<SkuImagesEntity> skuImagesEntities = sk.getImages().stream()
					.filter(i -> StringUtils.hasLength(i.getImgUrl())).map(img -> {
						SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
						skuImagesEntity.setImgUrl(img.getImgUrl());
						skuImagesEntity.setDefaultImg(img.getDefaultImg());
						skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
						return skuImagesEntity;
					}).collect(Collectors.toList());
			skuImagesService.saveBatch(skuImagesEntities);
			List<Attr> attrs = sk.getAttr();
			SkuReductionTo skuReductionTo = new SkuReductionTo();
			skuReductionTo.setSkuId(spuInfoEntity.getId());
			BeanUtils.copyProperties(sk, skuReductionTo);
			skuReductionTo.setMemberPrice(sk.getMemberPrice());
			couponRemoteClient.saveReduction(skuReductionTo);
			List<SkuSaleAttrValueEntity> attrValueEntities = attrs.stream().map(attr -> {
				SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
				attrValueEntity.setAttrName(attr.getAttrName());
				attrValueEntity.setAttrValue(attr.getAttrValue());
				attrValueEntity.setAttrId(attr.getAttrId());
				attrValueEntity.setSkuId(skuInfoEntity.getSkuId());
				return attrValueEntity;
			}).collect(Collectors.toList());
			skuSaleAttrValueService.batchSaveSaleAttrs(attrValueEntities);
			return skuInfoEntity;
		}).collect(Collectors.toList());
		return true;
	}

	@Override
	public void saveBasicInfo(SpuInfoEntity spuInfoEntity) {
		this.save(spuInfoEntity);
	}

	@Transactional(rollbackFor = {Throwable.class})
	@Override
	public void upSpuProduct(Long spuId) {
		SpuInfoEntity spuInfoEntity = this.getById(spuId);
		BrandEntity brand = brandService.getById(spuInfoEntity.getBrandId());
		Long catalogId = spuInfoEntity.getCatalogId();
		CategoryEntity category = categoryService.getById(catalogId);
		// 查询所有的SKU信息
		List<SkuInfoEntity> skus = skuInfoService.querySkusWithSpuId(spuId);
		// 查询所有的sku的属性信息
		List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.queryAttrsBySpuId(spuId);
		List<Long> skuAttrIds = productAttrValueEntities.stream().map(e -> e.getAttrId()).collect(Collectors.toList());
		skuAttrIds = attrService.findQuickShowAttrs(skuAttrIds);
		List<Long> finalSkuAttrIds = skuAttrIds;
		List<ProductAttrValueEntity> attrValueEntities = productAttrValueEntities.stream().filter(attr -> finalSkuAttrIds.contains(attr.getAttrId()))
				.collect(Collectors.toList());
		List<SkuEsModelTo.Attrs> attrs = attrValueEntities.stream().map(item -> {
			SkuEsModelTo.Attrs attr = new SkuEsModelTo.Attrs();
			BeanUtils.copyProperties(item, attr);
			return attr;
		}).collect(Collectors.toList());
		// 查询库存信息
		R r = wareFeignRemoteClient.stockQuery(skuAttrIds.toArray(new Long[]{}));
		List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {
		});
		Map<Long, Boolean> stockVoMap = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, skuStockVo -> skuStockVo.isHasStock()));
		//log.info("查询到的库存信息{}", data);
		List<SkuEsModelTo> models = skus.stream().map(sku -> {
			SkuEsModelTo model = new SkuEsModelTo();
			BeanUtils.copyProperties(sku, model);
			model.setSkuPrice(sku.getPrice());
			model.setSkuImg(sku.getSkuDefaultImg());
			model.setHasStock(false);
			// 设置是否还有库存信息
			model.setHasStock(Objects.isNull(stockVoMap.get(sku.getSkuId())) ? true : stockVoMap.get(sku.getSkuId()));
			model.setHotScore(0L);
			model.setBrandName(spuInfoEntity.getSpuName());
			model.setBrandImg(brand.getLogo());
			model.setCatalogName(category.getName());
			model.setAttrs(attrs);
			return model;
		}).collect(Collectors.toList());
		log.info("组装的信息{}", models);
		R result = searchFeignRemoteClient.storageSkuInfo(models);
		log.info("接收到的search服务的结果{}", result);
		spuInfoEntity.setPublishStatus(1);
		spuInfoEntity.setUpdateTime(new Date());
		this.baseMapper.updateById(spuInfoEntity);
	}


}