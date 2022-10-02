package it.workstocks.dto.email;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

	@Email
	@NotBlank
	private String to;

	@Size(min = 5)
	@NotBlank
	private String subject;

	@Size(min = 10)
	@NotBlank
	private String messageBody;
}
