package it.workstocks.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {
	private String token;
	
	private String oldPassword;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "{loginAndRegister.passwordRegex}")
	private String newPassword;
	
	@NotBlank
	private String confirmPassword;
}
