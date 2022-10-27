package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.CommentReplayDao;
import element.io.mall.product.entity.CommentReplayEntity;
import element.io.mall.product.service.CommentReplayService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("commentReplayService")
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayDao, CommentReplayEntity> implements CommentReplayService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}