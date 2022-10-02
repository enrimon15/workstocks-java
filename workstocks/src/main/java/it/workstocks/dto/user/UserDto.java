package it.workstocks.dto.user;

import java.util.Base64;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import it.workstocks.dto.BaseDto;
import it.workstocks.validator.email.EmailUniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends BaseDto<Long> {

	protected Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;
	
	@Email
	@EmailUniqueConstraint
	@NotBlank
	protected String email;
	
	private byte[] avatar;

	public String getBase64Avatar() {
		if (avatar == null) return null;
		return Base64.getEncoder().encodeToString(this.avatar);
	}
}
