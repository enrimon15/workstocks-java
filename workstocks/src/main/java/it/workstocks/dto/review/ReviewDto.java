package it.workstocks.dto.review;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import it.workstocks.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto extends BaseDto<ReviewKeyDto> {

	@Valid
	private ReviewKeyDto id;

	@NotNull
	@Range(min = 1, max = 5)
	private int rating;
	
	public ReviewDto(ReviewKeyDto key) {
		this.id = key;
	}
}
