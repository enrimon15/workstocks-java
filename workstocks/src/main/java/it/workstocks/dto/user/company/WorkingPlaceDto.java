package it.workstocks.dto.user.company;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import it.workstocks.dto.AddressDto;
import it.workstocks.dto.BaseDto;
import it.workstocks.entity.enums.WorkingPlaceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkingPlaceDto extends BaseDto<Long> {
	private Long id;
	
	@Valid
	@NotNull
	private AddressDto address;
	
	private boolean mainWorkingPlace;
	
	@NotNull
	private WorkingPlaceType workingPlaceType;
}
