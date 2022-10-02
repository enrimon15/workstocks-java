package it.workstocks.dto.user.applicant.cv;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.dto.BaseDto;
import it.workstocks.entity.enums.Assestment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillDto extends BaseDto<Long> {
	private Long id;
	
	@NotNull
	private Assestment assestment;
	
	@NotBlank
	@Size(min = 3, max = 60)
	private String name;
	
	public int getSkillLevel() {
        switch (this.assestment) {
            case BEGINNER:
                return 33;
            case INTERMEDIATE:
                return 66;
            case ADVANCED:
                return 100;
            default:
                return 0;
        }
    }
	
	public String getSkillColor() {
        switch (this.assestment) {
	        case BEGINNER:
	            return "danger";
	        case INTERMEDIATE:
	            return "warning";
	        case ADVANCED:
	            return "success";
	        default:
	            return null;
        }
    }
}
