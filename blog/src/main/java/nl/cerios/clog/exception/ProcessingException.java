package nl.cerios.clog.exception;

public class ProcessingException extends Exception {
	private static final long serialVersionUID = 5397175076392843689L;

	public ProcessingException(String message) {
		super(message);
	}
	
	public ProcessingException(Throwable cause) {
		super(cause);
	}
	
	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}