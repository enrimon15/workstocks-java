package it.workstocks.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import it.workstocks.validator.email.EmailUniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

	@Size(min = 3, max = 60)
	@NotBlank
	private String name;

	@Size(min = 3, max = 60)
	@NotBlank
	private String surname;

	@Email
	@NotBlank
	@EmailUniqueConstraint
	private String email;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "{loginAndRegister.passwordRegex}")
	private String password;

	private String passwordConfirmation;
}
