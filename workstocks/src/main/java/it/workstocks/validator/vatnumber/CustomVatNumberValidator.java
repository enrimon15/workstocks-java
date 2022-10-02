package it.workstocks.validator.vatnumber;

import static it.workstocks.utils.NumberUtils.isPositive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomVatNumberValidator implements ConstraintValidator<VatNumberConstraint, Long> {

	@Override
	public boolean isValid(Long valueVatNumber, ConstraintValidatorContext context) {
		if (valueVatNumber != null) {
			return isPositive(valueVatNumber) && String.valueOf(valueVatNumber).length() == 11;
		}
		
		return true;
	}

}
