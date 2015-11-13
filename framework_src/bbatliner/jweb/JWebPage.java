package bbatliner.jweb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.UUID;

import bbatliner.compiler.CompilationError;
import bbatliner.compiler.JavaCompilerFromString;
import bbatliner.http.HttpRequest;

/**
 * A utility class for rendering JWeb documents as HTML.
 * @author Brendan Batliner
 *
 */
public class JWebPage {
	
	/**
	 * The package name of the JWeb Renderer dynamic class used in the rendering process.
	 */
	private static final String JWEB_RENDERER_PACKAGE = "bbatliner";
	
	/**
	 * The class name of the JWeb Renderer dynamic class used in the rendering process.
	 */
	private static final String JWEB_RENDERER_CLASS_NAME = "JwebRenderer";
	
	/**
	 * The starting tag for a JWeb tag.
	 */
	private static final String SERVER_START_TAG = "<%";
	
	/**
	 * The ending tag for a JWeb tag.
	 */
	private static final String SERVER_END_TAG = "%>";
	
	/**
	 * The tag to mean "print this expression".
	 */
	private static final String PRINT_START_TAG = SERVER_START_TAG + "=";
	
	// If the original file contains "\r\n\t", this cannot be directly written
	// to the source code without causing compilation errors with multiline
	// strings, etc. So we encode our whitespace with UUIDs generated at runtime.
	/**
	 * A UUID to represent a new line in the pre-compiled JWeb source.
	 */
	private static final UUID NEW_LINE = UUID.randomUUID();
	/**
	 * A UUID to represent a carriage return in the pre-compiled JWeb source.
	 */
	private static final UUID CARRIAGE_RETURN = UUID.randomUUID();
	/**
	 * A UUID to represent a tab in the pre-compiled JWeb source.
	 */
	private static final UUID TAB = UUID.randomUUID();
	
	/**
	 * Encode the whitespace characters in a string with unique identifiers.
	 * @param s The string to encode.
	 * @return The string with its whitespace encoded.
	 */
	private static String encodeWhitespace(String s) {
		return s.replaceAll("\n", NEW_LINE.toString())
				.replaceAll("\r", CARRIAGE_RETURN.toString())
				.replaceAll("\t", TAB.toString());
	}
	
	/**
	 * Decode the whitespace in a string from unique identifiers to characters.
	 * @param s The string to decode.
	 * @return The string with its whitespace decoded.
	 */
	private static String decodeWhitespace(String s) {
		return s.replaceAll(NEW_LINE.toString(), "\n")
				.replaceAll(CARRIAGE_RETURN.toString(), "\r")
				.replaceAll(TAB.toString(), "\t");
	}

	/**
	 * Render the contents of a stream, printing the rendered result to a stream.
	 * Any whitespace or other extraneous characters are preserved.
	 * @param in The InputStream to read content from.
	 * @param out The OutputStream to print the rendered content to.
	 * @param request The HttpRequest object associated with this render request.
	 * @throws RenderException If rendering failed for any reason (couldn't read
	 * stream, couldn't render page, etc.)
	 */
	public static void render(InputStream in, PrintStream out, HttpRequest request) throws RenderException {
		// Read the contents of the input stream
		StringBuilder inString = new StringBuilder();

		/* read the contents of the stream */
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				inString.append(line);
			}
		} catch (IOException e) {
			throw new RenderException("Could not read the input stream", e);
		}
		
		String jweb = inString.toString();
		
		StringBuilder sourceCode = new StringBuilder();
		sourceCode.append("package ");
		sourceCode.append(JWEB_RENDERER_PACKAGE);
		sourceCode.append(";\n");
		sourceCode.append("public class ");
		sourceCode.append(JWEB_RENDERER_CLASS_NAME);
		sourceCode.append(" {\n");
		sourceCode.append("    public void render(java.io.PrintStream out, bbatliner.jweb.http.HttpRequest request) {\n");
//		sourceCode.append("        StringBuilder rendered = new StringBuilder;\n");

		while (jweb.length() > 0) {
			// If a server tag, parse the Java code
			if (jweb.startsWith(SERVER_START_TAG)) {
				String serverTag = jweb.substring(0, jweb.indexOf(SERVER_END_TAG));
				// Starts with a tag meaning "print this expression"
				if (jweb.startsWith(PRINT_START_TAG)) {
					String contents = serverTag.substring(PRINT_START_TAG.length() + 1, serverTag.length() - 1);
					//sourceCode.append("rendered += " + contents + ";\n");
					sourceCode.append("out.println(");
					sourceCode.append(contents);
					sourceCode.append(");");
				}
				// Starts with a generic Java code tag
				else {
					String contents = serverTag.substring(SERVER_START_TAG.length() + 1, serverTag.length() - 1);
					sourceCode.append(contents + "\n");
				}
				// Chew through the tag
				jweb = jweb.substring(jweb.indexOf(SERVER_END_TAG) + SERVER_END_TAG.length());
			}
			// Otherwise just add the HTML to the rendered document
			else {
				sourceCode.append("rendered += \"" + encodeWhitespace(jweb.substring(0, 1)) + "\";\n");
				jweb = jweb.substring(1); // chew through jweb, 1 character at a time
			}
		}
		
		sourceCode.append("        return rendered;\n");
		sourceCode.append("    }\n");
		sourceCode.append("}\n");
		
		String rendered = "";
		Class<?> rendererClass;
		try {
			rendererClass = JavaCompilerFromString.compile(
					String.join(".", JWEB_RENDERER_PACKAGE, JWEB_RENDERER_CLASS_NAME), sourceCode.toString());
			rendered = (String) rendererClass.getMethod("render").invoke(rendererClass.newInstance());
		} catch (CompilationError e) {
			throw new RenderException(e);
		} catch (Exception e) { // No other exceptions are "expected", so we can't handle them
			e.printStackTrace();
		}
		
		out.println(decodeWhitespace(rendered));
	}

}
