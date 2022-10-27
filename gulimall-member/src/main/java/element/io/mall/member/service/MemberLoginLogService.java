package element.io.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

