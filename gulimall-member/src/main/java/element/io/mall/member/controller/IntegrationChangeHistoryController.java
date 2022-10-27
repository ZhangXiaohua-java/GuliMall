package element.io.mall.member.controller;

import java.util.Arrays;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import element.io.mall.common.util.R;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.entity.IntegrationChangeHistoryEntity;
import element.io.mall.member.service.IntegrationChangeHistoryService;


/**
 * 积分变化历史记录
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
@RestController
@RequestMapping("member/integrationchangehistory")
public class IntegrationChangeHistoryController {
	@Autowired
	private IntegrationChangeHistoryService integrationChangeHistoryService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	//@RequiresPermissions("member:integrationchangehistory:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = integrationChangeHistoryService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("member:integrationchangehistory:info")
	public R info(@PathVariable("id") Long id) {
            IntegrationChangeHistoryEntity integrationChangeHistory = integrationChangeHistoryService.getById(id);

		return R.ok().put("integrationChangeHistory", integrationChangeHistory);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	//@RequiresPermissions("member:integrationchangehistory:save")
	public R save(@RequestBody IntegrationChangeHistoryEntity integrationChangeHistory) {
            integrationChangeHistoryService.save(integrationChangeHistory);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	//@RequiresPermissions("member:integrationchangehistory:update")
	public R update(@RequestBody IntegrationChangeHistoryEntity integrationChangeHistory) {
            integrationChangeHistoryService.updateById(integrationChangeHistory);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	//@RequiresPermissions("member:integrationchangehistory:delete")
	public R delete(@RequestBody Long[] ids) {
            integrationChangeHistoryService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
