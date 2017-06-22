package nl.cerios.clog.exception;

public class InvalidInputException extends Exception {
	private static final long serialVersionUID = -967099686662943579L;

	public InvalidInputException(String message) {
		super(message);
	}
	
	public InvalidInputException(Throwable cause) {
		super(cause);
	}
	
	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}