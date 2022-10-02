package it.workstocks.dto.search;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedRequest {
	public static final int PAGE_SIZE = 10;
	@Min(value = 1L)
	private int pageNumber;

	private FiltersDto filters;

}
