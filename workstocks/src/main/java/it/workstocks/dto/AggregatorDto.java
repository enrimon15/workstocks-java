package it.workstocks.dto;

import it.workstocks.utils.Operators;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregatorDto<T> {
	private Operators operator;
	private T value;
}
