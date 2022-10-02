package it.workstocks.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

	protected Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;
	
	@Email
	@NotBlank
	protected String email;
	
	private byte[] avatar;
}
