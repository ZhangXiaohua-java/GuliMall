package element.io.mall.ware.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张晓华
 * @date 2022-11-7
 */
@Configuration
public class InterceptorConfig {

	@Bean
	public MybatisPlusInterceptor interceptor() {
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
		PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
		mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
		paginationInnerInterceptor.setDbType(DbType.MYSQL);
		paginationInnerInterceptor.setMaxLimit(100L);
		paginationInnerInterceptor.setOverflow(true);
		return mybatisPlusInterceptor;
	}


}
