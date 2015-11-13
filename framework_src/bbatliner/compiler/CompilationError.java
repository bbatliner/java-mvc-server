package bbatliner.compiler;

/**
 * Indicates Java compilation failed.
 * @author Brendan Batliner
 *
 */
public class CompilationError extends Error {
	public CompilationError() {
		super();
	}
	public CompilationError(String message) {
		super(message);
	}
	public CompilationError(Throwable t) {
		super(t);
	}
}
