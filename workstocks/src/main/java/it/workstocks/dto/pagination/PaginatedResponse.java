package it.workstocks.dto.pagination;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginatedResponse {
	private static final int PAGE_SIZE = 10;
	private String data;
	private int pageNumber;
	private int totalPages;
	private long totalElements;
}
