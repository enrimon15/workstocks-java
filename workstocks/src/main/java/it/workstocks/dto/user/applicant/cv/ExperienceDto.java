package it.workstocks.dto.user.applicant.cv;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExperienceDto {
	private Long id;
	
	@NotBlank
	@Size(min = 3, max = 40)
	private String jobPosition;
	
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	private boolean inProgress;
	
	@NotBlank
	@Size(min = 3, max = 25)
	private String company;
	
	@Size(min = 3, max = 300)
	private String description;
}
