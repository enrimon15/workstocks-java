package it.workstocks.validator.url;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.workstocks.utils.StringUtils;

public class CustomUrlValidator implements ConstraintValidator<UrlConstraint, String> {

	@Override
	public boolean isValid(String valueURL, ConstraintValidatorContext context) {
		if (valueURL != null) {
			return StringUtils.isValidUrl(valueURL);
		}
		return true;
	}

}
