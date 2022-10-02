package it.workstocks.dto.pagination;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedDtoResponse<T> {

	private Set<T> elements;
	private PaginatedResponse response;
}
