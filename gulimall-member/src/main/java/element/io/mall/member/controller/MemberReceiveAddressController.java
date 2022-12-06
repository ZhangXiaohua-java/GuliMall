package element.io.mall.member.controller;

import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.member.entity.MemberReceiveAddressEntity;
import element.io.mall.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 会员收货地址
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
	@Autowired
	private MemberReceiveAddressService memberReceiveAddressService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("member:memberreceiveaddress:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = memberReceiveAddressService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("member:memberreceiveaddress:info")
	public R info(@PathVariable("id") Long id) {
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);
		return R.ok().put("memberReceiveAddress", memberReceiveAddress);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("member:memberreceiveaddress:save")
	public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
		memberReceiveAddressService.save(memberReceiveAddress);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("member:memberreceiveaddress:update")
	public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
		memberReceiveAddressService.updateById(memberReceiveAddress);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("member:memberreceiveaddress:delete")
	public R delete(@RequestBody Long[] ids) {
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	@ResponseBody
	@GetMapping("/{memberId}/address")
	public List<MemberReceiveAddressEntity> receiveAddress(@PathVariable Long memberId) {
		return memberReceiveAddressService.queryMemberAllAddress(memberId);
	}

}
