package bbatliner.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class HttpResponse {
	
	// TODO: Allow custom messages on the generic responses
	
	private static final String RESPONSE_400_BODY = new StringBuilder()
			.append("<!DOCTYPE html>")
			.append("<html>")
				.append("<body>")
					.append("<h1>400 BAD REQUEST</h1>")
					.append("<p>Please verify your request was formed correctly.</p>")
				.append("</body>")
			.append("</html>").toString();
	
	private static final String RESPONSE_404_BODY = new StringBuilder()
			.append("<!DOCTYPE html>")
			.append("<html>")
				.append("<body>")
					.append("<h1>404 NOT FOUND</h1>")
					.append("<p>The resource you requested could not be found.</p>")
				.append("</body>")
			.append("</html>").toString();
	
	private static final String RESPONSE_405_BODY = new StringBuilder()
			.append("<!DOCTYPE html>")
			.append("<html>")
				.append("<body>")
					.append("<h1>405 METHOD NOT ALLOWED</h1>")
					.append("<p>The specified method is not allowed for the requested resource.</p>")
				.append("</body>")
			.append("</html>").toString();
	
	private static final String RESPONSE_500_BODY = new StringBuilder()
			.append("<!DOCTYPE html>")
			.append("<html>")
				.append("<body>")
					.append("<h1>500 INTERNAL SERVER ERROR</h1>")
					.append("<p>An error occurred.</p>")
				.append("</body>")
			.append("</html>").toString();
	
	/**
	 * A generic 400 response.
	 */
	public static final HttpResponse RESPONSE_400 = new HttpResponse(400, "BAD REQUEST", new ByteArrayInputStream(RESPONSE_400_BODY.getBytes()));
	
	/**
	 * A generic 404 response.
	 */
	public static final HttpResponse RESPONSE_404 = new HttpResponse(404, "NOT FOUND", new ByteArrayInputStream(RESPONSE_404_BODY.getBytes()));
	
	/**
	 * A generic 405 response.
	 */
	public static final HttpResponse RESPONSE_405 = new HttpResponse(405, "METHOD NOT ALLOWED", new ByteArrayInputStream(RESPONSE_405_BODY.getBytes()));
	
	/**
	 * A generic 500 response.
	 */
	public static final HttpResponse RESPONSE_500 = new HttpResponse(500, "INTERNAL SERVER ERROR", new ByteArrayInputStream(RESPONSE_500_BODY.getBytes()));
	
	private int code;
	private String message;
	private InputStream content;
	
	/**
	 * Construct an empty HttpResponse. Use setters as necessary before sending.
	 */
	public HttpResponse() {
		
	}
	
	/**
	 * Construct a full HttpResponse, ready for sending.
	 * @param code The status code of the response.
	 * @param message The message associate with this code/response.
	 * @param content The content to send with this response.
	 */
	public HttpResponse(int code, String message, InputStream content) {
		this.code = code;
		this.message = message;
		this.content = content;
	}
	
	// TODO: Allow more control with the response headers, etc.
	/**
	 * Send this HttpResponse to the specified output stream, complete with headers.
	 * @param out The output stream to print this response to.
	 * @throws IOException If this response's content stream could not be read.
	 */
	public void sendResponse(PrintWriter out) throws IOException {
		// Print headers
		out.print("HTTP/1.1 ");
		out.print(this.getCode());
		out.print(" ");
		out.print(this.getMessage());
		out.println();
		out.println("Connection: keep-alive");
		out.println("Cache-Control: no-store");
		out.println("Content-type: text/html"); // TODO: Metadata/user specified content-type
		
		// Load all of the content (unfortunately has to go through a String for Content-length and Content-type)
		StringBuilder content = new StringBuilder();
		int c;
		while ((c = this.getContent().read()) != -1) {
			content.append((char) c);
		}
		
		// Print content
		out.print("Content-length: ");
		out.println(content.length());
		out.println();
		out.println(content.toString());
	}	

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public InputStream getContent() {
		return content;
	}
	public void setContent(InputStream content) {
		this.content = content;
	}
}
