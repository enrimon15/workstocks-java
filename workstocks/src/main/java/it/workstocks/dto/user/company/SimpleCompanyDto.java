package it.workstocks.dto.user.company;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.dto.AddressDto;
import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleCompanyDto {
	private Long id;
	
	@Size(min = 3, max = 60)
	@NotBlank
	private String name;
	
	@Size(min = 2, max = 5)
	@NotBlank
	private String companyForm;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer employeesNumber;
	
	@NotBlank
	@UrlConstraint
	private String website;
	
	private AddressDto address;
	
	private String photo;
	
	private String detailsURL;
}
