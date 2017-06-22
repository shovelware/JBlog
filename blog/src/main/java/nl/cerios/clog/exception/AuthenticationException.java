package nl.cerios.clog.exception;

public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -967099686662943579L;

	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
	
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}