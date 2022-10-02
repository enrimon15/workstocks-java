package it.workstocks.dto.user.company;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.dto.BaseDto;
import it.workstocks.validator.url.UrlConstraint;
import it.workstocks.validator.vatnumber.VatNumberConstraint;
import it.workstocks.validator.vatnumber.VatNumberUniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleCompanyDto extends BaseDto<Long> {
	private Long id;
	
	@Size(min = 3, max = 60)
	@NotBlank
	private String name;
	
	@NotNull
	@VatNumberConstraint
	@VatNumberUniqueConstraint
	private Long vatNumber;
	
	@Size(min = 2, max = 5)
	@NotBlank
	private String companyForm;
	
	@Size(min = 3, max = 5000)
	private String overview;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer employeesNumber;
	
	@Min(value = 1700)
	@NotNull
	private Integer foundationYear;
	
	@NotBlank
	@UrlConstraint
	private String website;
	
	private SimpleCompanyOwnerDto companyOwner;
	
	@Size(min = 3, max = 80)
	private String slogan;
	
	@NotBlank
	@Size(min = 7, max = 11)
	private String telephone;
	
	@Valid
	private WorkingPlaceDto mainWorkingPlace;
}
