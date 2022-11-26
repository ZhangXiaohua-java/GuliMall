package element.io.mall.member.controller;

import element.io.mall.common.to.LoginTo;
import element.io.mall.common.to.OauthLoginTo;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.member.entity.MemberEntity;
import element.io.mall.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


/**
 * 会员
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
@Slf4j
@RestController
@RequestMapping("member/member")
public class MemberController {

	@Autowired
	private MemberService memberService;


	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("member:member:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = memberService.queryPage(params);
		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("member:member:info")
	public R info(@PathVariable("id") Long id) {
		MemberEntity member = memberService.getById(id);

		return R.ok().put("member", member);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("member:member:save")
	public R save(@RequestBody MemberEntity member) {
		memberService.save(member);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("member:member:update")
	public R update(@RequestBody MemberEntity member) {
		memberService.updateById(member);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("member:member:delete")
	public R delete(@RequestBody Long[] ids) {
		memberService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@PostMapping("/register")
	public R register(@RequestBody MemberEntity member) {
		log.info("接收到的to信息{}", member);
		if (memberService.saveRegistedMemberInfo(member)) {
			log.info("新注册的会员信息{}", member);
			return R.ok();
		} else {
			return R.error();
		}
	}

	@PostMapping("/login")
	public R login(@RequestBody LoginTo loginTo) {
		MemberEntity member = memberService.memberLogin(loginTo);
		if (Objects.nonNull(member)) {
			return R.ok().put("data", member);
		} else {
			return R.error();
		}
	}

	@PostMapping("/oauth/login")
	public R oauthLogin(@RequestBody OauthLoginTo to) {
		MemberEntity member = memberService.oauthLogin(to);
		log.info("oauth返回的数据{}", member);
		return R.ok().put("data", member);
	}


}
