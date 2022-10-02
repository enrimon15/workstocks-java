package it.workstocks.dto.user.applicant.cv;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.entity.enums.Assestment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillDto {
	private Long id;
	
	@NotNull
	private Assestment assestment;
	
	@NotBlank
	@Size(min = 3, max = 60)
	private String name;
}
