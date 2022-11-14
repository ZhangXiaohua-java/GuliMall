package element.io.mall.ware.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.ware.entity.PurchaseEntity;
import element.io.mall.ware.service.PurchaseService;
import element.io.mall.ware.vo.PurchaseMergeVo;
import element.io.mall.ware.vo.PurchaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 采购信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
	@Autowired
	private PurchaseService purchaseService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("ware:purchase:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("ware:purchase:info")
	public R info(@PathVariable("id") Long id) {
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("ware:purchase:save")
	public R save(@RequestBody PurchaseEntity purchase) {
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("ware:purchase:update")
	public R update(@RequestBody PurchaseEntity purchase) {
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("ware:purchase:delete")
	public R delete(@RequestBody Long[] ids) {
		purchaseService.removeByIds(Arrays.asList(ids));
		return R.ok();
	}

	@PostMapping("/merge")
	public R merge(@RequestBody PurchaseMergeVo vo) {
		purchaseService.merge(vo);
		return R.ok();
	}

	@GetMapping("/unreceive/list")
	public R unreceiveList(Map<String, Object> params) {
		PageUtils pageUtils = purchaseService.queryUnreceiveListForPage(params);
		return R.ok().put("page", pageUtils);
	}

	@GetMapping("/receive/{id}")
	public R receivePurchaseOrder(@PathVariable Long id) {
		purchaseService.receivePurchaseOrder(id);
		return R.ok();
	}

	@PostMapping("/task/report")
	public R reportTaskStatus(@RequestBody PurchaseVo purchaseVo) {
		purchaseService.savePurchaseTaskProgress(purchaseVo);
		return R.ok();
	}

}
