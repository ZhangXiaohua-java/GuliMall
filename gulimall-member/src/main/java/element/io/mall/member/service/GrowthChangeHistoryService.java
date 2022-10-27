package element.io.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:45:39
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

	PageUtils queryPage(Map<String, Object> params);
}

