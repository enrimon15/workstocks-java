package it.workstocks.dto.search;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import it.workstocks.entity.enums.ContractType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltersDto {
	@Size(min = 2, max = 80)
	private String name;
	
	@Size(min = 2, max = 80)
	private String nameOrSurname;
	
	@Size(min = 2, max = 80)
	private String address;
	
	@Size(min = 2, max = 80)
	private String jobTitle;
	
	@Size(min = 2, max = 80)
	private String companyName;
	
	@Size(min = 2, max = 80)
	private String skill;
	
	@Size(min = 2, max = 80)
	private ContractType offerType;
	
	@Min(0)
	private Integer employeesNumber;
	
	@Min(0)
	private Integer foundationYear;
	
	@Min(0)
	private Integer experience;
	
	@Min(0)
	private Integer salary;
	
	@Size(min = 2, max = 80)
	private String newsSearchQuery;
}
