package element.io.search.controller;

import element.io.search.service.SearchService;
import element.io.search.vo.SearchParamVo;
import element.io.search.vo.SearchResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 张晓华
 * @date 2022-11-17
 */
@Controller
public class IndexController {

	@Resource
	private SearchService searchService;

	@GetMapping({"/search.html", "/list.html", "/"})
	public String index(SearchParamVo searchParamVo) throws IOException {
		SearchResultVo searchResultVo = searchService.searchProducts(searchParamVo);
		return "search";
	}


}
