package it.workstocks.validator.email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.workstocks.repository.UserRepository;
import it.workstocks.security.Roles;
import it.workstocks.utils.AuthUtility;

@Component
public class CustomEmailUniqueValidator implements ConstraintValidator<EmailUniqueConstraint, String> {

	@Autowired 
	private UserRepository userRepository;
	
	@Override
	public boolean isValid(String emailValue, ConstraintValidatorContext context) {		
		boolean duplicateEmail = userRepository.existsByEmail(emailValue);

		if (AuthUtility.getAuth() != null && AuthUtility.hasRole(Roles.APPLICANT)) { // modifica profilo
			if (emailValue != null && !emailValue.equals(AuthUtility.getCurrentUser().getEmail())) {
				return !duplicateEmail;
			}
		} else { // registrazione
			if (emailValue != null) {
				return !duplicateEmail;
			}
		}
		
		return true;
	}

}
