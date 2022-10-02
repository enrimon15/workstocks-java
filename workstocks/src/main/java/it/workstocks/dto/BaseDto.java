package it.workstocks.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseDto<E extends Serializable> {
	
	public abstract E getId();
	public abstract void setId(E id);

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
