package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

