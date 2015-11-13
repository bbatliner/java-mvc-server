package bbatliner.http;

/**
 * An internal exception to indicate 404 Not Found.
 * @author Brendan Batliner
 *
 */
public class NotFoundException extends Exception {
	public NotFoundException() {
		super();
	}
	public NotFoundException(Throwable e) {
		super(e);
	}
	public NotFoundException(String msg) {
		super(msg);
	}
}
