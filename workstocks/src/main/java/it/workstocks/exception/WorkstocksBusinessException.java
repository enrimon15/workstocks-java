package it.workstocks.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import it.workstocks.dto.error.ErrorValidationModel;

public class WorkstocksBusinessException extends Exception {

	private static final long serialVersionUID = 6088700353655434737L;
	private HttpStatus status;
	private List<ErrorValidationModel> errors;
	

	public WorkstocksBusinessException() {
		super();
	}

	public WorkstocksBusinessException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WorkstocksBusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkstocksBusinessException(String message) {
		super(message);
	}

	public WorkstocksBusinessException(Throwable cause) {
		super(cause);
	}
	
	public WorkstocksBusinessException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
	public WorkstocksBusinessException(String message, List<ErrorValidationModel> errors, HttpStatus status) {
		super(message);
		this.status = status;
		this.errors = errors;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public List<ErrorValidationModel> getErrors() {
		return errors;
	}
}
