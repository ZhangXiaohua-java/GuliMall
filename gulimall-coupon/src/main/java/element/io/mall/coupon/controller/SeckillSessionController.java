package element.io.mall.coupon.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.coupon.entity.SeckillSessionEntity;
import element.io.mall.coupon.service.SeckillSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 秒杀活动场次
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {
	@Autowired
	private SeckillSessionService seckillSessionService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("coupon:seckillsession:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = seckillSessionService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("coupon:seckillsession:info")
	public R info(@PathVariable("id") Long id) {
		SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

		return R.ok().put("seckillSession", seckillSession);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("coupon:seckillsession:save")
	public R save(@RequestBody SeckillSessionEntity seckillSession) {
		seckillSessionService.save(seckillSession);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("coupon:seckillsession:update")
	public R update(@RequestBody SeckillSessionEntity seckillSession) {
		seckillSessionService.updateById(seckillSession);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("coupon:seckillsession:delete")
	public R delete(@RequestBody Long[] ids) {
		seckillSessionService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
