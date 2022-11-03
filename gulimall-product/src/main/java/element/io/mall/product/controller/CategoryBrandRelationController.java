package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.CategoryBrandRelationEntity;
import element.io.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌分类关联
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("/product/categorybrandrelation")
public class CategoryBrandRelationController {
	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:categorybrandrelation:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = categoryBrandRelationService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:categorybrandrelation:info")
	public R info(@PathVariable("id") Long id) {
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

		return R.ok().put("categoryBrandRelation", categoryBrandRelation);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:categorybrandrelation:save")
	public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		if (categoryBrandRelationService.saveRelation(categoryBrandRelation)) {
			return R.ok();
		} else {
			return R.error();
		}
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:categorybrandrelation:update")
	public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
		categoryBrandRelationService.updateById(categoryBrandRelation);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:categorybrandrelation:delete")
	public R delete(@RequestBody Long[] ids) {
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@GetMapping("/catelog/list")
	public R catelogList(@RequestParam Long brandId) {
		List<CategoryBrandRelationEntity> list = categoryBrandRelationService.findCategoryBrandRelation(brandId);
		return R.ok().put("data", list);
	}


}
