package it.workstocks.dto.user;

import java.util.Base64;

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

	public String getBase64Avatar() {
		if (avatar == null) return null;
		return Base64.getEncoder().encodeToString(this.avatar);
	}
}
