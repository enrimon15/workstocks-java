package it.workstocks.dto;

import java.util.Optional;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
	
	private Optional<@Size(min=3, max = 50) String> street;
	
	private Optional<@Size(min=3, max = 50) String> city;
	
	private Optional<@Size(min=2, max = 2) String> country;
}
