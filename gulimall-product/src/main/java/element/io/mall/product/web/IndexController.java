package element.io.mall.product.web;

import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import element.io.mall.product.vo.CatelogLevel2Vo;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@Controller
public class IndexController {

	@Resource
	private CategoryService categoryService;


	@GetMapping("/")
	public String index(Model model) {
		List<CategoryEntity> categories = categoryService.findLevel1Categories();
		model.addAttribute("categories", categories);
		return "index";
	}


	@ResponseBody
	@GetMapping("/categories")
	public Map<String, List<CatelogLevel2Vo>> getCatelogies() throws IOException {
		Map<String, List<CatelogLevel2Vo>> categories = categoryService.findCategories();
		File file = new File("D:/data.json");
		FileUtils.write(file, categories.toString(), StandardCharsets.UTF_8);
		return categories;
	}

	
	@ResponseBody
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}


}
