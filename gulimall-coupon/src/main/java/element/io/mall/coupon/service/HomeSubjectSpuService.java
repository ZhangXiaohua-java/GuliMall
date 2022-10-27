package element.io.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

