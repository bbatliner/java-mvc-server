package bbatliner.http;

import java.util.ArrayList;
import java.util.List;

import bbatliner.jweb.Parameters;

/**
 * A wrapper class for an HTTP request, which includes information
 * such as the method, resource, and headers.
 * @author Brendan Batliner
 *
 */
public class HttpRequest {

	private String method;
	private String resource;
	private String protocol;
	private List<HttpHeader> headers = new ArrayList<>();
	private Parameters parameters = new Parameters();
	
	public HttpRequest(String requestLine) {
		String[] vals = requestLine.split(" ");
		this.method = vals[0];
		this.resource = vals[1];
		this.protocol = vals[2];
	}
	
	public void addHeader(String raw) {
		headers.add(new HttpHeader(raw));
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public List<HttpHeader> getHeaders() {
		return headers;
	}
	
	/**
	 * Retrieve a request header by name.
	 * @param name The name of the header to find.
	 * @return The header, if it exists, or null.
	 */
	public HttpHeader getHeaderByName(String name) {
		for (HttpHeader header : headers) {
			if (header.getName().toLowerCase().equals(name.toLowerCase())) {
				return header;
			}
		}
		return null;
	}
}
