package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.CategoryDao;
import element.io.mall.product.entity.CategoryEntity;
import element.io.mall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

}