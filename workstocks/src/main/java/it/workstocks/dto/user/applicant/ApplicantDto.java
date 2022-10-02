package it.workstocks.dto.user.applicant;

import it.workstocks.dto.user.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantDto extends UserDto {
	private String jobTitle;
}
