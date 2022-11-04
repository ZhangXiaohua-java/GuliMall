package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.service.AttrService;
import element.io.mall.product.vo.AttrResponseVo;
import element.io.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品属性
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
	@Autowired
	private AttrService attrService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:attr:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = attrService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrId}")
	//@RequiresPermissions("product:attr:info")
	public R info(@PathVariable("attrId") Long attrId) {
		AttrResponseVo vo = attrService.getDetail(attrId);
		return R.ok().put("attr", vo);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:attr:save")
	public R save(@RequestBody AttrVo attrVo) {
		if (attrService.saveAttr(attrVo)) {
			return R.ok();
		} else {
			return R.error();
		}


	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:attr:update")
	public R update(@RequestBody AttrVo vo) {
		if (attrService.updateInfo(vo)) {
			return R.ok();
		} else {
			return R.error();
		}

	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:attr:delete")
	public R delete(@RequestBody Long[] attrIds) {
		attrService.removeByIds(Arrays.asList(attrIds));

		return R.ok();
	}


	@GetMapping("/base/list/{catelogId}")
	public R baseList(@PathVariable Long catelogId, @RequestParam Map<String, Object> params) {
		params.put("catlogId", catelogId);
		PageUtils page = attrService.queryForPage(params);
		return R.ok().put("page", page);
	}

	@GetMapping("/sale/list/{catelogId}")
	public R saleList(@PathVariable Long catelogId, @RequestParam Map<String, Object> params) {
		params.put("catlogId", catelogId);
		params.put("type", "sale");
		PageUtils page = attrService.queryForPage(params);
		return R.ok().put("page", page);
	}
	
}
