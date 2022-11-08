package element.io.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import element.io.mall.common.util.PageUtils;
import element.io.mall.ware.entity.PurchaseEntity;
import element.io.mall.ware.vo.PurchaseMergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:53:27
 */
public interface PurchaseService extends IService<PurchaseEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void merge(PurchaseMergeVo vo);


	PageUtils queryUnreceiveListForPage(Map<String, Object> params);


	boolean receivePurchaseOrder(Long id);

}

