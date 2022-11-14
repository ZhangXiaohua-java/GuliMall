package element.io.mall.product.app.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品三级分类
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Slf4j
@RestController
@RequestMapping("product/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;


	@GetMapping("/list/tree")
	public R listAllCategoriesWithTree() {
		List<CategoryEntity> entities = categoryService.listCategoriesWithTree();
		return R.ok().put("data", entities);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:category:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = categoryService.queryPage(params);
		return R.ok().put("page", page);
	}

	@PostMapping("/update/batch")
	public R updateBatch(@RequestBody List<CategoryEntity> categoryEntities) {
		boolean saveBatch = categoryService.updateBatchById(categoryEntities);
		if (saveBatch) {
			return R.ok();
		} else {
			return R.error();
		}
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{catId}")
	//@RequiresPermissions("product:category:info")
	public R info(@PathVariable("catId") Long catId) {
		CategoryEntity category = categoryService.getById(catId);
		return R.ok().put("category", category);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:category:save")
	public R save(@RequestBody CategoryEntity category) {
		log.info("接收到的参数{}", category);
		boolean save = categoryService.save(category);
		if (save) {
			return R.ok();
		} else {
			return R.error();
		}
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:category:update")
	public R update(@RequestBody CategoryEntity category) {
		categoryService.updateCategoryInfoCaseCade(category);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:category:delete")
	public R delete(@RequestBody Long[] catIds) {
		boolean hasRemove = categoryService.removeByIds(Arrays.asList(catIds));
		if (hasRemove) {
			return R.ok();
		} else {
			return R.error();
		}
	}


}
