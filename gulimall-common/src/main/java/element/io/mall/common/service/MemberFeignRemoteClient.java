package element.io.mall.common.service;

import element.io.mall.common.to.LoginTo;
import element.io.mall.common.to.MemberReceiveAddressTo;
import element.io.mall.common.to.OauthLoginTo;
import element.io.mall.common.to.RegisterTo;
import element.io.mall.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-22
 */
@FeignClient(value = "gulimall-member")
public interface MemberFeignRemoteClient {

	@PostMapping("/member/member/register")
	public R register(@RequestBody RegisterTo to);


	@PostMapping("/member/member/login")
	public R login(@RequestBody LoginTo loginTo);

	@PostMapping("/member/member/oauth/login")
	R oauthLogin(@RequestBody OauthLoginTo to);

	@GetMapping("/member/memberreceiveaddress/{memberId}/address")
	List<MemberReceiveAddressTo> receiveAddress(@PathVariable("memberId") Long memberId);


	@RequestMapping("/member/memberreceiveaddress/info/{id}")
	R getAddress(@PathVariable("id") Long id);


}
