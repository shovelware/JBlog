package nl.cerios.clog.exception;

public class DataNotFoundException extends Exception {
	private static final long serialVersionUID = -967099686662943579L;

	public DataNotFoundException(String message) {
		super(message);
	}
	
	public DataNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DataNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
