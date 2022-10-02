package it.workstocks.dto.pagination;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginatedResponse {
	private int pageSize;
	private int pageNumber;
	private int totalPages;
	private long totalElements;
}
