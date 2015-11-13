package bbatliner.http;

import java.io.PrintWriter;

/**
 * An interface for a request handler. This handler has one function, handle,
 * which is responsible for taking a request and any parameters and
 * generating an HTTP response.
 * @author Brendan Batliner
 *
 */
public interface RouteHandler {
	/**
	 * Implementations should implement this method, which handles any functionality
	 * that needs to occur when a route is hit, including rendering and creating an HTTP response.
	 * @param route The Route object this RouteHandler is registered to.
	 * @param request The HttpRequest associated with the triggered route.
	 * @param out The PrintWriter this handler should write the HttpResponse to.
	 * @throws Exception Any generic exception that could be thrown by the handler.
	 * Calling methods should check for more specific exceptions depending on the handler used.
	 */
	HttpResponse handle(Route route, HttpRequest request, PrintWriter out) throws Exception;
}
