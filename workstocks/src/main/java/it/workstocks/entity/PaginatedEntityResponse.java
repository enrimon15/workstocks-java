package it.workstocks.entity;

import java.util.Set;

import it.workstocks.dto.pagination.PaginatedResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedEntityResponse<T extends BaseEntity<?>> {
	
	private Set<T> elements;
	private PaginatedResponse reponse;
}
