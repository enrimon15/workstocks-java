package it.workstocks.validator;

import static it.workstocks.utils.NumberUtils.isNotPositive;
import static it.workstocks.utils.StringUtils.isBlank;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.workstocks.dto.registration.Registration;
import it.workstocks.dto.registration.UserType;

public class RegistrationFormValidator implements Validator {

	private static final String MISSING_PASSWORD_MATCH = "loginAndRegister.passwordDoesNotMatch";
	private static final String NOT_BLANK = "javax.validation.constraints.NotBlank.message";
	private static final String NOT_NULL = "javax.validation.constraints.NotNull.message";
	private static final String NUMBER_VALIDATION = "loginAndRegister.numberValidation";

	@Override
	public boolean supports(Class<?> clazz) {
		return Registration.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		Registration registrationForm = (Registration) target;

		if (UserType.COMPANY_OWNER.equals(registrationForm.getUserType())) {
			if (isBlank(registrationForm.getCompanyName())) {
				errors.rejectValue("companyName", NOT_BLANK);
			}
			if (isBlank(registrationForm.getCompanyForm())) {
				errors.rejectValue("companyForm", NOT_BLANK);
			}
			if (isBlank(registrationForm.getContactPhone())) {
				errors.rejectValue("contactPhone", NOT_BLANK);
			}
			if (isNotPositive(registrationForm.getEmployeesNumber())) {
				errors.rejectValue("employeesNumber", NUMBER_VALIDATION);
			}
			if (isNotPositive(registrationForm.getFoundationYear())) {
				errors.rejectValue("foundationYear", NUMBER_VALIDATION);
			}
			if (isBlank(registrationForm.getWebsite())) {
				errors.rejectValue("website", NOT_BLANK);
			}
			if (isBlank(registrationForm.getAddress())) {
				errors.rejectValue("address", NOT_BLANK);
			}
			if (isBlank(registrationForm.getCity())) {
				errors.rejectValue("city", NOT_BLANK);
			}
			if (isBlank(registrationForm.getCountry())) {
				errors.rejectValue("country", NOT_BLANK);
			}
			if (registrationForm.getWorkingPlaceType() == null) {
				errors.rejectValue("workingPlaceType", NOT_NULL);
			}
		}


		if (!registrationForm.getPassword().equals(registrationForm.getPasswordConfirmation())) {
			errors.rejectValue("passwordConfirmation", MISSING_PASSWORD_MATCH);
		}

	}

}
