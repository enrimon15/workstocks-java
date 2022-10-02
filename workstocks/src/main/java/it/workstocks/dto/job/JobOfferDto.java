package it.workstocks.dto.job;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import it.workstocks.dto.BaseDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.entity.enums.ContractType;
import it.workstocks.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobOfferDto extends BaseDto<Long> {
	private Long id;

	@Valid
	private SimpleCompanyDto company;
	
	@Size(min = 3, max = 100)
	private String title;
	
	@Size(min = 3, max = 5000)
	private String description;
	
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dueDate;
	
	@NotNull
	private ContractType contractType;
	
	@NotNull
	private Gender gender;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer minSalary;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer maxSalary;
	
	@Min(value = 0L)
	@NotNull
	private Short experience;
	
	private WorkingPlaceDto workingPlace;
	
	@Valid
	private Set<SkillDto> skills;
	
	private String[] skillFromFE;

}
