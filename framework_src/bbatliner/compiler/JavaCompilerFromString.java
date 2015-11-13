package bbatliner.compiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 * A Java compiler that compiles source code from a String.
 * @author Brendan Batliner
 *
 */
public class JavaCompilerFromString {
	
	private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	/**
	 * Compile the provided source code into a class of the provided name.
	 * @param className The name of the class to compile.
	 * @param sourceCode The source code of the class.
	 * @return A compiled Class object.
	 * @throws CompilationError If compilation failed for any reason.
	 * @throws ClassNotFoundException If the class loader could not find the compiled class.
	 */
	public static Class<?> compile(String className, String sourceCode) throws CompilationError, ClassNotFoundException {
		// Redirect stderr to capture any compilation errors
		ByteArrayOutputStream baosStderr = new ByteArrayOutputStream();
		PrintStream stderr = System.err;
		PrintStream myStderr = new PrintStream(baosStderr);
		System.setErr(myStderr);
		
		// Compile the sourceCode
		try {
			JavaSourceFromString source = new JavaSourceFromString(className, sourceCode);
			ClassManager classManager = new ClassManager(compiler.getStandardFileManager(null, null, null));
			Iterable<? extends JavaFileObject> sources = Arrays.asList(source);
			boolean compiledSuccessfully = compiler.getTask(null, classManager, null, null, null, sources).call();
			// Check compilation result
			if (!compiledSuccessfully) {
				throw new CompilationError(baosStderr.toString());
			}
			// Load the compiled class
			return classManager.getClassLoader(null).loadClass(className);
		} catch (Exception e) {
			throw e;
		} finally {
			// Return stderr to the System default
			System.setErr(stderr);
		}		
	}

}
