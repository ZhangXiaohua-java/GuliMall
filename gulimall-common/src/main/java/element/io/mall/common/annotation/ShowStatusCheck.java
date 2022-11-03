package element.io.mall.common.annotation;

import element.io.mall.common.validator.ShowStatusCheckValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author 张晓华
 * @date 2022-11-1
 */
@Constraint(validatedBy = {ShowStatusCheckValidator.class})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
public @interface ShowStatusCheck {

	String message() default "{element.io.mall.common.annotation.ShowStatusCheck.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int[] enumVals() default {};

}
