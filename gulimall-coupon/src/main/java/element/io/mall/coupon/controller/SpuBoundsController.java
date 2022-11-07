package element.io.mall.coupon.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.coupon.entity.SpuBoundsEntity;
import element.io.mall.coupon.service.SpuBoundsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品spu积分设置
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
@RestController
@RequestMapping("coupon/spubounds")
public class SpuBoundsController {
	@Autowired
	private SpuBoundsService spuBoundsService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("coupon:spubounds:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuBoundsService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("coupon:spubounds:info")
	public R info(@PathVariable("id") Long id) {
		SpuBoundsEntity spuBounds = spuBoundsService.getById(id);

		return R.ok().put("spuBounds", spuBounds);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("coupon:spubounds:save")
	public R save(@RequestBody SpuBoundsEntity spuBounds) {
		spuBoundsService.save(spuBounds);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("coupon:spubounds:update")
	public R update(@RequestBody SpuBoundsEntity spuBounds) {
		spuBoundsService.updateById(spuBounds);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("coupon:spubounds:delete")
	public R delete(@RequestBody Long[] ids) {
		spuBoundsService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@PostMapping("/detail/save")
	public R saveDetailReductionInfo(@RequestBody SpuBoundsEntity boundsEntity) {
		boundsEntity.setWork(1);
		spuBoundsService.save(boundsEntity);
		return R.ok();
	}

}
