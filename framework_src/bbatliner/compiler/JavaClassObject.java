package bbatliner.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * A JavaFileObject that stores byte code in memory, as opposed to using a .class file.
 * @author Brendan Batliner
 *
 */
public class JavaClassObject extends SimpleJavaFileObject {
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	public JavaClassObject(String name, Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
	}
	
	public byte[] getBytes() {
		return baos.toByteArray();
	}
	
	/**
	 * Provide the byte array to the JDK when it requests it.
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		return baos;
	}
}
