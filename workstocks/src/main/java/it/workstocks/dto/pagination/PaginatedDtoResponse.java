package it.workstocks.dto.pagination;

import java.util.Set;

import it.workstocks.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedDtoResponse<T extends BaseDto<?>> {

	private Set<T> elements;
	private PaginatedResponse response;
}
