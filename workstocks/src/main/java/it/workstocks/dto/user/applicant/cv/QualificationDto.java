package it.workstocks.dto.user.applicant.cv;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import it.workstocks.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualificationDto extends BaseDto<Long> {
	private Long id;	
	
	@NotBlank
	@Size(min = 3, max = 80)
	private String name;
	
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	private boolean inProgress;
	
	@NotBlank
	@Size(min = 3, max = 40)
	private String institute;
	
	@Size(min = 3, max = 15)
	private String valuation;
	
	@Size(min = 3, max =300)
	private String description;
}
