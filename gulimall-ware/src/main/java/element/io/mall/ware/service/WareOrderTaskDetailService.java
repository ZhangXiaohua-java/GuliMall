package element.io.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

