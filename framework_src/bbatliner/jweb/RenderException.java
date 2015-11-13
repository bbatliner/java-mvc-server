package bbatliner.jweb;

/**
 * Indicates a page was unable to be rendered.
 * @author Brendan Batliner
 *
 */
public class RenderException extends Exception {
	public RenderException() {
		
	}
	public RenderException(String message) {
		super(message);
	}
	public RenderException(Throwable e) {
		super(e);
	}
	public RenderException(String message, Throwable e) {
		super(message, e);
	}
}
