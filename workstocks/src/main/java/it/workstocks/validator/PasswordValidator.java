package it.workstocks.validator;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.workstocks.dto.user.PasswordDto;
import it.workstocks.security.UserDetailsImpl;
import it.workstocks.utils.AuthUtility;

public class PasswordValidator implements Validator {
	
	private static final String MISSING_PASSWORD_MATCH = "changePassword.errorConfirmPassword";
	private static final String CURRENT_PASSWORD_MATCH = "changePassword.errorCurrentPassword";
	
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
				errors.rejectValue("oldPassword", CURRENT_PASSWORD_MATCH);
			}
		}		
		
		if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", MISSING_PASSWORD_MATCH);
		}
	}
}
