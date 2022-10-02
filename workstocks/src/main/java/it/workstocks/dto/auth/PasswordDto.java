package it.workstocks.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordDto {
	private String token;
	
	private String oldPassword;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "{loginAndRegister.passwordRegex}")
	private String newPassword;
	
	@NotBlank
	private String confirmPassword;
	
	public PasswordDto(String pw, String confirmPw) {
		this.newPassword = pw;
		this.confirmPassword = confirmPw;
	}
}
