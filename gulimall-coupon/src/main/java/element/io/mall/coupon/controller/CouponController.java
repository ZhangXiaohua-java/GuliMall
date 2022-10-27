package element.io.mall.coupon.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.coupon.entity.CouponEntity;
import element.io.mall.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 优惠券信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
@RestController
@RequestMapping("coupon/coupon")
public class CouponController {
	@Autowired
	private CouponService couponService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("coupon:coupon:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = couponService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("coupon:coupon:info")
	public R info(@PathVariable("id") Long id) {
		CouponEntity coupon = couponService.getById(id);

		return R.ok().put("coupon", coupon);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("coupon:coupon:save")
	public R save(@RequestBody CouponEntity coupon) {
		couponService.save(coupon);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("coupon:coupon:update")
	public R update(@RequestBody CouponEntity coupon) {
		couponService.updateById(coupon);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("coupon:coupon:delete")
	public R delete(@RequestBody Long[] ids) {
		couponService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
