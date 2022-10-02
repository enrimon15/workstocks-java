package it.workstocks.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String password;
}
