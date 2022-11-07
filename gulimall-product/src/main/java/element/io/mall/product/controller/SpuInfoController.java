package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.SpuInfoEntity;
import element.io.mall.product.service.SpuInfoService;
import element.io.mall.product.vo.SpuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * spu信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
	@Autowired
	private SpuInfoService spuInfoService;

	/**
	 * 列表
	 * // http://127.0.0.1/api/product/spuinfo/list?t=1667789010334&page=1&limit=10
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:spuinfo:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = spuInfoService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("product:spuinfo:info")
	public R info(@PathVariable("id") Long id) {
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

		return R.ok().put("spuInfo", spuInfo);
	}

	/**
	 * 保存
	 */
	//http://127.0.0.1/api/product/spuinfo/save
	@PostMapping("/save")
	public R save(@RequestBody SpuInfoVo spuInfoVo) {
		if (spuInfoService.saveSpuInfo(spuInfoVo)) {
			return R.ok();
		} else {
			return R.error();
		}

	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:spuinfo:update")
	public R update(@RequestBody SpuInfoEntity spuInfo) {
		spuInfoService.updateById(spuInfo);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:spuinfo:delete")
	public R delete(@RequestBody Long[] ids) {
		spuInfoService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}


}
