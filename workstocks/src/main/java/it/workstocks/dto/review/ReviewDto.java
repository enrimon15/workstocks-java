package it.workstocks.dto.review;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;
	private boolean isReviewed;
}
