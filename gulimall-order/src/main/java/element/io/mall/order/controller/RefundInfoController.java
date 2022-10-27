package element.io.mall.order.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.order.entity.RefundInfoEntity;
import element.io.mall.order.service.RefundInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 退款信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 19:05:36
 */
@RestController
@RequestMapping("order/refundinfo")
public class RefundInfoController {
	@Autowired
	private RefundInfoService refundInfoService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = refundInfoService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		RefundInfoEntity refundInfo = refundInfoService.getById(id);

		return R.ok().put("refundInfo", refundInfo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")

	public R save(@RequestBody RefundInfoEntity refundInfo) {
		refundInfoService.save(refundInfo);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody RefundInfoEntity refundInfo) {
		refundInfoService.updateById(refundInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		refundInfoService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
