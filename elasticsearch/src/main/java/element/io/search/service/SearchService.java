package element.io.search.service;

import element.io.search.vo.SearchParamVo;
import element.io.search.vo.SearchResultVo;

import java.io.IOException;

/**
 * @author 张晓华
 * @date 2022-11-17
 */
public interface SearchService {


	SearchResultVo searchProducts(SearchParamVo searchParamVo) throws IOException;

}
