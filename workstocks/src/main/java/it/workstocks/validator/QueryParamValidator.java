package it.workstocks.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import it.workstocks.dto.error.ErrorValidationModel;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Component
public class QueryParamValidator {
	
	@Autowired
	private Translator translator;
	
	public void validateInteger(String fieldName, Integer value, Integer min, Integer max) throws WorkstocksBusinessException {
		List<ErrorValidationModel> errors = new ArrayList<>();
		
		if (min != null && value < min) {
			String message = translator.toLocale(ErrorUtils.INVALID_NUMBER_MIN, new Integer[] {min});
			errors.add(new ErrorValidationModel(fieldName, value, message));
		}
		
		if (max != null && value > max) {
			String message = translator.toLocale(ErrorUtils.INVALID_NUMBER_MAX, new Integer[] {max});
			errors.add(new ErrorValidationModel(fieldName, value, message));
		}
		
		if (!errors.isEmpty()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.INVALID_REQUEST_PARAM, null), errors, HttpStatus.BAD_REQUEST);
		}
	}

}
