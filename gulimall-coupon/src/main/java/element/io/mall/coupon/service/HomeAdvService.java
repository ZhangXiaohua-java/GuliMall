package element.io.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

