package it.workstocks.dto.search;

import java.util.List;

import javax.validation.constraints.Size;

import it.workstocks.dto.AggregatorDto;
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
	
	private List<AggregatorDto<Integer>> employeesNumber;
	private List<AggregatorDto<Integer>> foundationYear;
	private List<AggregatorDto<Integer>> experience;
	private List<AggregatorDto<Integer>> salary;
	
	@Size(min = 2, max = 80)
	private String newsSearchQuery;
}
