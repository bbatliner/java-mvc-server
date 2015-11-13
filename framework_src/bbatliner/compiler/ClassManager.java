package bbatliner.compiler;

import java.security.SecureClassLoader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

/**
 * A custom JavaFileManager that holds the custom JavaClassObjects.
 * @author Brendan Batliner
 *
 */
public class ClassManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private JavaClassObject classObject;
	
	public ClassManager(StandardJavaFileManager manager) {
		super(manager);
	}
	
	/**
	 * Returns a class loader that simply returns the byte code of this FileManager's internal
	 * JavaClassObject. Doesn't actually search for classes.
	 */
	@Override
	public ClassLoader getClassLoader(Location location) {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				byte[] b = classObject.getBytes();
				return super.defineClass(name, b, 0, b.length);
			}
		};
	}
	
	/**
	 * Return this FileManager's internal JavaClassObject when the JDK requests it.
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) {
		classObject = new JavaClassObject(className, kind);
		return classObject;
	}
}
