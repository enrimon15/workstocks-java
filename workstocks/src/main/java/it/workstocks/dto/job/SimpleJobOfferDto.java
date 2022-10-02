package it.workstocks.dto.job;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.entity.Address;
import it.workstocks.entity.enums.ContractType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleJobOfferDto {
	
	private Long id;
	
	private LocalDateTime createdAt;

	@Valid
	private SimpleCompanyDto company;
	
	@Size(min = 3, max = 100)
	private String title;
	
	@NotNull
	private ContractType contractType;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer minSalary;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer maxSalary;
	
	private Set<String> skills;
	
	private String detailsURL;
	
	private Address address;

}
