package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.SkuInfoEntity;
import element.io.mall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * sku信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
	@Autowired
	private SkuInfoService skuInfoService;

	/**
	 * 列表
	 * page=1&limit=10&key=&catelogId=0&brandId=0&min=0&max=0
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = skuInfoService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{skuId}")
	//@RequiresPermissions("product:skuinfo:info")
	public R info(@PathVariable("skuId") Long skuId) {
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

		return R.ok().put("skuInfo", skuInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:skuinfo:save")
	public R save(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.save(skuInfo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:skuinfo:update")
	public R update(@RequestBody SkuInfoEntity skuInfo) {
		skuInfoService.updateById(skuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:skuinfo:delete")
	public R delete(@RequestBody Long[] skuIds) {
		skuInfoService.removeByIds(Arrays.asList(skuIds));

		return R.ok();
	}

}
