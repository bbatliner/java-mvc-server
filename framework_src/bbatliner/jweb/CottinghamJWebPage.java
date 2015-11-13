package bbatliner.jweb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbatliner.compiler.CompilationError;
import bbatliner.compiler.JavaCompilerFromString;
import bbatliner.http.HttpRequest;

public class CottinghamJWebPage {

	private static final String MODEL_ANNOTATION_PATTERN = "<%@model (.+?)%>";
	private static final String SERVER_TAG_PATTERN = "<%(.+?)%>";

	private static final String TEMPLATE = "package jweb;"
			+ "public class WebRender {"
			+ "public static void doRender(java.io.PrintStream out, --MODEL_TYPE-- model, bbatliner.http.HttpRequest request) {"
			+ "--BODY--" + "}" + "}";

	public static void render(InputStream in, PrintStream out, Object model, HttpRequest request) throws RenderException, ClassNotFoundException {
		StringBuffer doc = new StringBuffer();

		/* read the contents of the stream */
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				doc.append(line);
			}
		} catch (IOException ioe) {
			throw new RenderException("Could not read the input stream", ioe);
		}
		
		// Try to find the type of the model, if it's annotated.
		Class<?> classOfModel = Object.class;
		Pattern p1 = Pattern.compile(MODEL_ANNOTATION_PATTERN);
		Matcher m1 = p1.matcher(doc);
		if (m1.find()) {
			String typeOfModel = m1.group(1).trim();
			classOfModel = Class.forName(typeOfModel);
			doc.delete(m1.start(), m1.end());
		}

		StringBuffer renderStatements = new StringBuffer();
		Pattern p2 = Pattern.compile(SERVER_TAG_PATTERN);
		Matcher matcher = p2.matcher(doc);
		/* if the document contains scriptlet tags, parse it */
		if ( matcher.find() ) {
			int previousEnd = 0;

			do {
				String statements = matcher.group(1).trim();
				int end = matcher.end();

				renderStatements.append("out.println(\"");
				renderStatements.append(doc.substring(previousEnd, end - matcher.group().length()).trim());
				renderStatements.append("\");");

				if (statements.startsWith("=")) {
					renderStatements.append("out.println(");
					renderStatements.append(statements.substring(1).trim());
					renderStatements.append(");");
				} else {
					renderStatements.append(statements);
				}

				previousEnd = end;
			} while ( matcher.find() );
			
			/* Emit the remainder of the doc */
			renderStatements.append("out.println(\"");
			renderStatements.append(doc.substring(previousEnd));
			renderStatements.append("\");");
			
		} else { /* otherwise, just emit the document */
			renderStatements.append("out.println(\"");
			renderStatements.append(doc);
			renderStatements.append("\");");
		}

		try {
			/* compile the class with the various statements making up the render method body */
			Class<?> rendererClass = JavaCompilerFromString.compile("jweb.WebRender",
					TEMPLATE.replaceAll("--BODY--", renderStatements.toString())
							.replaceAll("--MODEL_TYPE--", classOfModel.getName()));

			/* get the doRender method using reflection */
			Method method = rendererClass.getMethod("doRender", PrintStream.class, classOfModel, HttpRequest.class);
			/* invoke the method with the provided outputstream as well as the HTTP request.
			 * NOTE: this makes the variable 'request' available to the view template and 
			 * allows for things like <%= request.getParameter("foo") %> once/if that class
			 * implements a parameter map.
			 */
			method.invoke(rendererClass.newInstance(), out, model, request);
		} catch (CompilationError e) {
			throw new RenderException(e);
		} catch (Exception e) { // No other exceptions are "expected", so we
			// can't handle them
			throw new RuntimeException(e);
		}
	}
}