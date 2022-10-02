package it.workstocks.dto.search;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedRequest {
	@Min(value = 1L)
	private int pageNumber;
	
	@Min(value = 1L)
	private int pageSize;

	private FiltersDto filters;

}
