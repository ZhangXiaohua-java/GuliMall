package element.io.secskill.service;

import element.io.mall.common.to.SeckillSkuRelationTo;

import java.util.List;

/**
 * @author 张晓华
 * @date 2022-12-11
 */
public interface SecKillService {


	List<SeckillSkuRelationTo> queryCuurentSecKillProducts();

	SeckillSkuRelationTo isInSecKill(Long skuId);
	
}
