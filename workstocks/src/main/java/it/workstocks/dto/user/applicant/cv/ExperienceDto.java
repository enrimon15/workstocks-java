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
public class ExperienceDto extends BaseDto<Long> {
	private Long id;
	
	@NotBlank
	@Size(min = 3, max = 40)
	private String jobPosition;
	
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	private boolean inProgress;
	
	@NotBlank
	@Size(min = 3, max = 25)
	private String company;
	
	@Size(min = 3, max = 300)
	private String description;
}
