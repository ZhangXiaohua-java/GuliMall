package element.io.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.service.MemberFeignRemoteClient;
import element.io.mall.common.to.MemberReceiveAddressTo;
import element.io.mall.common.util.DataUtil;
import element.io.mall.common.util.PageUtils;
import element.io.mall.common.util.R;
import element.io.mall.ware.dao.WareInfoDao;
import element.io.mall.ware.entity.WareInfoEntity;
import element.io.mall.ware.service.WareInfoService;
import element.io.mall.ware.vo.CourierVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"all"})
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

	@Resource
	private MemberFeignRemoteClient memberFeignRemoteClient;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String key = Objects.nonNull(params.get("key")) ? params.get("key").toString() : null;
		int pageNum = Objects.nonNull(params.get("page")) ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = Objects.nonNull(params.get("limit")) ? Integer.parseInt(params.get("limit").toString()) : 10;
		Page<WareInfoEntity> page = new Page<>(pageNum, pageSize);
		LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(StringUtils.hasText(key), WareInfoEntity::getId, key)
				.or()
				.eq(StringUtils.hasText(key), WareInfoEntity::getAreacode, key)
				.or()
				.like(StringUtils.hasText(key), WareInfoEntity::getName, key);
		this.baseMapper.selectPage(page, wrapper);
		return new PageUtils(page.getRecords(), Long.valueOf(page.getTotal()).intValue(), pageSize, pageNum);
	}


	@Override
	public CourierVo countFee(Long addId) {
		CourierVo vo = new CourierVo();
		R r = memberFeignRemoteClient.getAddress(addId);
		//
		MemberReceiveAddressTo address = DataUtil.typeConvert(r.get("memberReceiveAddress"), new TypeReference<MemberReceiveAddressTo>() {
		});
		vo.setAddress(address);
		vo.setPrice(new BigDecimal(ThreadLocalRandom.current().nextInt(50)));
		return vo;
	}


}