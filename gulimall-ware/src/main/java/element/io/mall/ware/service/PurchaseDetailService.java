package element.io.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<PurchaseDetailEntity> batchFindPurchaseDetailInfo(List<Long> detailItemIds);

	void batchUpdatePurchaseDetailInfo(List<PurchaseDetailEntity> detailEntities);

	void updatePurchaseDetailStatus(Long id);
}

