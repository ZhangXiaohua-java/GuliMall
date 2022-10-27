package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SpuCommentDao;
import element.io.mall.product.entity.SpuCommentEntity;
import element.io.mall.product.service.SpuCommentService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuCommentService")
public class SpuCommentServiceImpl extends ServiceImpl<SpuCommentDao, SpuCommentEntity> implements SpuCommentService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}