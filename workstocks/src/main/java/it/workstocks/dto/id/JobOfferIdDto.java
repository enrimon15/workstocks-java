package it.workstocks.dto.id;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobOfferIdDto {
	
	@Min(1L)
	private Long jobOfferId;
}
