package element.io.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

