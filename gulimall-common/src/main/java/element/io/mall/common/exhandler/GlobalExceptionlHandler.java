package element.io.mall.common.exhandler;

import element.io.mall.common.enumerations.ExceptionStatusEnum;
import element.io.mall.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @author 张晓华
 * @date 2022-11-1
 */
@SuppressWarnings({"all"})
@RestControllerAdvice
@Slf4j
public class GlobalExceptionlHandler {

	@ExceptionHandler
	public R defaultHandler(Throwable throwable) {
		log.error("捕获到未处理的异常{}", throwable.getMessage());
		throwable.printStackTrace();
		return R.error().put("error", throwable.getMessage());
	}

	// 校验异常处理器
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public R vaildationExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
		log.error("数据校验异常{}", methodArgumentNotValidException.getBindingResult().toString());
		BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
		HashMap<String, String> hashMap = new HashMap<>();
		bindingResult.getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String defaultMessage = error.getDefaultMessage();
			hashMap.put(fieldName, defaultMessage);
		});
		return R.excepion(ExceptionStatusEnum.PARAM_NOT_VALID_EXCEPTION).put("data", hashMap);
	}


}
