package it.workstocks.dto.error;

import java.time.LocalDateTime;
import java.util.List;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@ToString
@Getter
@Setter
public class ErrorResponse {
	
	private LocalDateTime timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
	private List<ErrorValidationModel> validationErrors;

}
