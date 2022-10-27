package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.SpuImagesEntity;
import element.io.mall.product.service.SpuImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * spu图片
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/spuimages")
public class SpuImagesController {
	@Autowired
	private SpuImagesService spuImagesService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:spuimages:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuImagesService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:spuimages:info")
	public R info(@PathVariable("id") Long id) {
		SpuImagesEntity spuImages = spuImagesService.getById(id);

		return R.ok().put("spuImages", spuImages);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:spuimages:save")
	public R save(@RequestBody SpuImagesEntity spuImages) {
		spuImagesService.save(spuImages);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:spuimages:update")
	public R update(@RequestBody SpuImagesEntity spuImages) {
		spuImagesService.updateById(spuImages);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:spuimages:delete")
	public R delete(@RequestBody Long[] ids) {
		spuImagesService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
