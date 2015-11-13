package bbatliner.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import bbatliner.jweb.Parameters;
import bbatliner.reflection.ObjectMarshaller;

/**
 * An implementation of the HTTP protocol. Keeps track of requests and serves
 * responses according to an MVC pattern.
 * @author Brendan Batliner
 *
 */
public class HttpProtocol {
	
	private String staticContentDir;
	private File[] applications;
	private HttpRequest request;
	private boolean requestComplete = false;
	private boolean processingHeaders = true;
	
	/**
	 * Construct a new protocol with the provided parameters.
	 * @param applications The array of Files (applications) to serve.
	 * @param staticContentDir The directory where static content is stored (typically static_content).
	 */
	public HttpProtocol(File[] applications, String staticContentDir) {
		this.applications = applications;
		this.staticContentDir = staticContentDir;
	}

	/**
	 * Listen for HTTP requests and process them.
	 * @param in The BufferedReader from where input is coming.
	 * @param out The PrintWriter to where output, if any, should be written.
	 * @throws IOException If the input stream could not be read.
	 */
	public void listen(BufferedReader in, PrintWriter out) throws IOException {
		String input;
		// While the client socket is still sending non-null data, process and reply
		while ((input = in.readLine()) != null) {
			HttpResponse httpResponse = null;
			do {
				// If the HTTP request is still sending headers, parse them
				if (processingHeaders) {
					// Detect the empty line that marks the end of the headers
					if (input.length() == 0) {
						processingHeaders = false;
						continue;
					}
					if (request == null) { // if this is the first line of input, create the request
						request = new HttpRequest(input);
					} else { // otherwise add the header
						request.addHeader(input);
					}
					// Read the next line
					input = in.readLine();
				}
				// Parse the parameters, if present, and construct a response
				else {
					try {
						// Parse URL parameters
						// TODO: Should this be limited to specific methods (GET/POST) or other criteria?
						if (request.getResource().contains("?")) {
							int i = request.getResource().indexOf('?');
							String queryString = request.getResource().substring(i + 1);
							request.setParameters(Parameters.parseQueryString(queryString));
							// Remove the query string from the request
							request.setResource(request.getResource().substring(0, i));
						}
						
						// Parse body as JSON Object
						// TODO: Should this be limited to specific methods (GET/POST) or other criteria?
						String body = "";
						HttpHeader contentType = request.getHeaderByName("Content-type");
						if (contentType != null) {
							int len = Integer.parseInt(request.getHeaderByName("Content-length").getValue());
							for (int i = 0; i < len; i++) {
								body += (char) in.read();
							}
						}
						JSONObject bodyObj = null;
						if (!body.isEmpty()) {
							try {
								bodyObj = new JSONObject(body);
							} catch (JSONException e) {
								throw new BadRequestException(e);
							}
						}
						
						// Find the requested web application
						String[] urlComponents = request.getResource().split("/");
						File requestedJar = null;
						if (urlComponents.length > 0) {
							urlComponents = Arrays.copyOfRange(urlComponents, 1, urlComponents.length); // remove the empty string at index 0
							String requestedApp = urlComponents[0];
							for (File jar : applications) {
								if (jar.getName().startsWith(requestedApp)) {
									requestedJar = jar;
									break;
								}
							}
						}
						
						// Find the resource within the specified web application
						// The client needs to specify an app and some further information (controller or resource)
						if (requestedJar == null || urlComponents.length <= 1) {
							throw new NotFoundException();
						}
						
						// TODO: Annotations on the controller method params (@RequestParamter, @FormParameter, @URLParameter, etc.)
						// TODO: Annotations on the controller itself (@Controller, @Action)
						// TODO: Add support for www-form-urlencoded (for POSTs that actually redirect!)
						
						// Try to instantiate a controller by the name
						ClassLoader loader = URLClassLoader.newInstance(new URL[] { requestedJar.toURI().toURL() }, getClass().getClassLoader());
						// TODO: How strict should this route finding be? i.e., should 'home' go to the same controller as 'hOmE'? (I'm thinking no.)
						String controllerName = urlComponents[1];
						controllerName = Character.toUpperCase(controllerName.charAt(0)) + controllerName.substring(1) + "Controller";
						controllerName = String.join(".", "controllers", controllerName);
						try {
							Class<?> controller = loader.loadClass(controllerName);
							
							// Construct and load the HttpResponse from the controller
							Constructor<?> constructor = controller.getDeclaredConstructor(HttpRequest.class, ClassLoader.class);
							Object c = constructor.newInstance(request, loader);
							String requestedMethod = urlComponents.length >= 3 ? urlComponents[2] : "index";
							if (bodyObj != null) request.getParameters().addJSONProperties(bodyObj);
							
							httpResponse = (HttpResponse) ObjectMarshaller.invokeMethod(c, requestedMethod, request.getParameters());
						}
						// If the controller couldn't be found, try to serve the resource directly from static_content
						catch (ClassNotFoundException e) {
							// Serve static_content
							String resource = String.join("/", Arrays.copyOfRange(urlComponents, 1, urlComponents.length));
							resource = String.join("/", staticContentDir, resource);
							
							InputStream inResource = loader.getResourceAsStream(resource);
							if (inResource == null) {
								throw new NotFoundException();
							} else {
								httpResponse = new HttpResponse(200, "OK", inResource);
							}
						}
					}
					// Catch errors that correspond to HTTP response codes
					catch (BadRequestException e) {
						httpResponse = HttpResponse.RESPONSE_400;
					}
					catch (NotFoundException e) {
						httpResponse = HttpResponse.RESPONSE_404;
					}
					// Any other exception is a legitimate error
					catch (Exception e) {
						httpResponse = HttpResponse.RESPONSE_500;
						e.printStackTrace();
					}
					
					request = null; // reset the request obj
					requestComplete = true; // break the while loop
					// 404 by default
					if (httpResponse == null) {
						httpResponse = HttpResponse.RESPONSE_404;
					}					
				}
			} while (!requestComplete);
			
			// Reset the state of this protocol object
			requestComplete = false;
			processingHeaders = true;
			httpResponse.sendResponse(out);
		}
	}
}
