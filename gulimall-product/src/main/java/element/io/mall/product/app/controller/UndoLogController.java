package element.io.mall.product.app.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.UndoLogEntity;
import element.io.mall.product.service.UndoLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/undolog")
public class UndoLogController {
	@Autowired
	private UndoLogService undoLogService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:undolog:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = undoLogService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:undolog:info")
	public R info(@PathVariable("id") Long id) {
		UndoLogEntity undoLog = undoLogService.getById(id);

		return R.ok().put("undoLog", undoLog);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:undolog:save")
	public R save(@RequestBody UndoLogEntity undoLog) {
		undoLogService.save(undoLog);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:undolog:update")
	public R update(@RequestBody UndoLogEntity undoLog) {
		undoLogService.updateById(undoLog);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:undolog:delete")
	public R delete(@RequestBody Long[] ids) {
		undoLogService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
