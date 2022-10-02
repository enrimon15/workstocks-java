package it.workstocks.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.security.UserDetailsImpl;
import it.workstocks.utils.AuthUtility;

@Component
public class PasswordValidator implements Validator {
	
	private static final String MISSING_PASSWORD_MATCH = "changePassword.errorConfirmPassword";
	private static final String CURRENT_PASSWORD_MATCH = "changePassword.errorCurrentPassword";
	
	@Autowired
	private ResourceBundleMessageSource source;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return PasswordDto.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		PasswordDto passwordDto = (PasswordDto) target;
		
		if (!AuthUtility.hasRole("ROLE_ANONYMOUS")) { // cambio pw da loggato
			PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
			if (!pwdEncoder.matches(passwordDto.getOldPassword(), ( (UserDetailsImpl) AuthUtility.getAuth().getPrincipal()).getPassword() )) {
				errors.rejectValue("oldPassword", "old password not valid" , source.getMessage(CURRENT_PASSWORD_MATCH, null, LocaleContextHolder.getLocale()));
			}
		}		
		
		if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "confirm password doesn't match", source.getMessage(MISSING_PASSWORD_MATCH, null, LocaleContextHolder.getLocale()));
		}
	}
}
