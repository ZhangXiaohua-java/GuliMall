package element.io.sso.client.annotation;

import java.lang.annotation.*;

/**
 * @author 张晓华
 * @date 2022-11-26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PermissionCheck {

	public boolean needLogin() default true;


}
