package element.io.mall.product.app.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.SpuCommentEntity;
import element.io.mall.product.service.SpuCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品评价
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/spucomment")
public class SpuCommentController {
	@Autowired
	private SpuCommentService spuCommentService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:spucomment:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuCommentService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:spucomment:info")
	public R info(@PathVariable("id") Long id) {
		SpuCommentEntity spuComment = spuCommentService.getById(id);

		return R.ok().put("spuComment", spuComment);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:spucomment:save")
	public R save(@RequestBody SpuCommentEntity spuComment) {
		spuCommentService.save(spuComment);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:spucomment:update")
	public R update(@RequestBody SpuCommentEntity spuComment) {
		spuCommentService.updateById(spuComment);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:spucomment:delete")
	public R delete(@RequestBody Long[] ids) {
		spuCommentService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
