package element.io.search.service;

import element.io.mall.common.to.SkuEsModelTo;

import java.io.IOException;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
public interface IEsStorageService {


	boolean storageSkuInfo(List<SkuEsModelTo> skuEsModelTos) throws IOException;


}
