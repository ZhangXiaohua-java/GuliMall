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
import element.io.mall.member.entity.MemberLoginLogEntity;
import element.io.mall.member.service.MemberLoginLogService;


/**
 * 会员登录记录
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
@RestController
@RequestMapping("member/memberloginlog")
public class MemberLoginLogController {
	@Autowired
	private MemberLoginLogService memberLoginLogService;

	/**
     * 列表
     */
	@RequestMapping("/list")
	//@RequiresPermissions("member:memberloginlog:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = memberLoginLogService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
     * 信息
     */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("member:memberloginlog:info")
	public R info(@PathVariable("id") Long id) {
            MemberLoginLogEntity memberLoginLog = memberLoginLogService.getById(id);

		return R.ok().put("memberLoginLog", memberLoginLog);
	}

	/**
     * 保存
     */
	@RequestMapping("/save")
	//@RequiresPermissions("member:memberloginlog:save")
	public R save(@RequestBody MemberLoginLogEntity memberLoginLog) {
            memberLoginLogService.save(memberLoginLog);

		return R.ok();
	}

	/**
     * 修改
     */
	@RequestMapping("/update")
	//@RequiresPermissions("member:memberloginlog:update")
	public R update(@RequestBody MemberLoginLogEntity memberLoginLog) {
            memberLoginLogService.updateById(memberLoginLog);

		return R.ok();
	}

	/**
     * 删除
     */
	@RequestMapping("/delete")
	//@RequiresPermissions("member:memberloginlog:delete")
	public R delete(@RequestBody Long[] ids) {
            memberLoginLogService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
