package it.workstocks.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewAverageAndCountPojo {
	private double ratingAverage;
	private long reviewNumber;
}
