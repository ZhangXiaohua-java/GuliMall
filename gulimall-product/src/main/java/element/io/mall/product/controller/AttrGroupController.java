package element.io.mall.product.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.product.entity.AttrEntity;
import element.io.mall.product.entity.AttrGroupEntity;
import element.io.mall.product.service.AttrGroupService;
import element.io.mall.product.vo.AttrGroupRelationVo;
import element.io.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
	@Autowired
	private AttrGroupService attrGroupService;

	/**
	 * 列表
	 */
	@RequestMapping("/list/{catId}")
	//@RequiresPermissions("product:attrgroup:list")
	public R list(@RequestParam Map<String, Object> params, @PathVariable Long catId) {
		params.put("catId", catId);
		PageUtils page = attrGroupService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	//@RequiresPermissions("product:attrgroup:info")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
		AttrGroupEntity attrGroup = attrGroupService.findById(attrGroupId);
		return R.ok().put("attrGroup", attrGroup);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:attrgroup:save")
	public R save(@RequestBody AttrGroupEntity attrGroupEntity) {
		if (attrGroupService.save(attrGroupEntity)) {
			return R.ok();
		} else {
			return R.error();
		}
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:attrgroup:update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:attrgroup:delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

		return R.ok();
	}


	@GetMapping("/{attrGroupId}/attr/relation")
	public R queryRelations(@PathVariable Long attrGroupId, @RequestParam Map<String, Object> params) {
		params.put("attrGroupId", attrGroupId);
		List<AttrEntity> data = attrGroupService.queryRelationsByAttrGroupId(params);
		return R.ok().put("data", data);
	}

	//	 attrgroup/6/noattr/relation

	@GetMapping("/{attrGroupId}/noattr/relation")
	public R queryNoRelationAttr(@PathVariable Long attrGroupId, @RequestParam Map<String, Object> params) {
		params.put("attrGroupId", attrGroupId);
		PageUtils page = attrGroupService.queryNoRelationAttrs(params);
		return R.ok().put("page", page);
	}

	@PostMapping("/attr/relation")
	public R addRelation(@RequestBody AttrGroupRelationVo[] relationVos) {
		if (relationVos.length != 0 && attrGroupService.saveRelations(relationVos)) {
			return R.ok();
		} else {
			return R.error(403, "提交非法数据");
		}
	}

	@PostMapping("/attr/relation/delete")
	public R deleteRelation(@RequestBody AttrGroupRelationVo[] relationVos) {
		if (relationVos.length != 0 && attrGroupService.batchDeleteRelations(relationVos)) {
			return R.ok();
		} else {
			return R.error(403, "提交非法数据");
		}

	}

//	 http://127.0.0.1/api/product/attrgroup/225/withattr?t=1667722129436

	@GetMapping("/{catelogId}/withattr")
	public R getAttrGroupWithAttrs(@PathVariable Long catelogId) {
		List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrs(catelogId);
		return R.ok().put("data", vos);
	}


}
