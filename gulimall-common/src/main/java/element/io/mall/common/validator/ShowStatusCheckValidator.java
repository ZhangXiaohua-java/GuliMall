package element.io.mall.common.validator;

import element.io.mall.common.annotation.ShowStatusCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-1
 */
public class ShowStatusCheckValidator implements ConstraintValidator<ShowStatusCheck, Integer> {

	private List<Integer> enumVals;

	@Override
	public void initialize(ShowStatusCheck constraintAnnotation) {
		if (constraintAnnotation.enumVals().length == 0) {
			this.enumVals = new ArrayList<>();
			this.enumVals.add(0);
		} else {
			this.enumVals = new ArrayList<>();
			Arrays.stream(constraintAnnotation.enumVals())
					.distinct()
					.forEach(num -> enumVals.add(num));
		}
	}


	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (enumVals.contains(value)) {
			return true;
		}
		return false;
	}


}
