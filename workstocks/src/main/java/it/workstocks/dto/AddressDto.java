package it.workstocks.dto;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
	@Size(min=3, max = 50)
	private String street;
	
	@Size(min=3, max = 60)
	private String city;
	
	@Size(min=2, max = 2)
	private String country;
}
