package element.io.mall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;


import element.io.mall.coupon.dao.CouponDao;
import element.io.mall.coupon.entity.CouponEntity;
import element.io.mall.coupon.service.CouponService;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}