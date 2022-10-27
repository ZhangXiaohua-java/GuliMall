package element.io.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

