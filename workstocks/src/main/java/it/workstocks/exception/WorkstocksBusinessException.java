package it.workstocks.exception;

public class WorkstocksBusinessException extends Exception {

	private static final long serialVersionUID = 6088700353655434737L;

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

}
