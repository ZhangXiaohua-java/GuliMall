package element.io.mall.ware.controller;

import element.io.mall.common.to.SkuStockVo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.ware.entity.WareSkuEntity;
import element.io.mall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
	@Autowired
	private WareSkuService wareSkuService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("ware:waresku:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wareSkuService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("ware:waresku:info")
	public R info(@PathVariable("id") Long id) {
		WareSkuEntity wareSku = wareSkuService.getById(id);

		return R.ok().put("wareSku", wareSku);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("ware:waresku:save")
	public R save(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.save(wareSku);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("ware:waresku:update")
	public R update(@RequestBody WareSkuEntity wareSku) {
		wareSkuService.updateById(wareSku);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("ware:waresku:delete")
	public R delete(@RequestBody Long[] ids) {
		wareSkuService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@PostMapping("/stock/query")
	public R stockQuery(@RequestBody Long[] skuIds) {
		List<SkuStockVo> wareInfoEntities = wareSkuService.queryStock(skuIds);
		return R.ok().put("data", wareInfoEntities);
	}


}
