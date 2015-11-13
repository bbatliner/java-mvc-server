package bbatliner.http;

/**
 * An internal exception to indicate 400 Bad Request.
 * @author Brendan Batliner
 *
 */
public class BadRequestException extends Exception {
	public BadRequestException() {
		super();
	}
	public BadRequestException(Throwable e) {
		super(e);
	}
	public BadRequestException(String msg) {
		super(msg);
	}
}
