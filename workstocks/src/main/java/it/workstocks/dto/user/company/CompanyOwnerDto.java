package it.workstocks.dto.user.company;

import javax.validation.Valid;

import it.workstocks.dto.user.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyOwnerDto extends UserDto {
	@Valid
	private CompanyDto company;
}
