package it.workstocks.dto.user.applicant.cv;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurriculumDto {
	private boolean autogenerate;
	@Size(min = 1)
	private String curriculum;
}
