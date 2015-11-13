package bbatliner.jweb;

import static bbatliner.http.HttpMethod.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import bbatliner.http.Route;
import bbatliner.http.Routes;

/**
 * An HTTP server designed to serve JWeb files, a modified version of JSP.
 * @author Brendan Batliner
 *
 */
public class JWebServer {
	
	/**
	 * The directory that the server will look in for static assets, i.e. anything
	 * served to the client (HTML, CSS, images, etc.).
	 */
	public final static String STATIC_CONTENT_DIR = "static_content";
	
	/**
	 * The default port the server will bind to, if none is specified.
	 */
	public final static int DEFAULT_PORT = 8080;
	
	/**
	 * The default root directory of the web server, if none is specified.
	 */
	public final static String DEFAULT_WEB_ROOT = "."; // TODO: does this actually work lol
	
	private String webRoot;
	private int port;
	private Routes routes = new Routes();
	private boolean hasStarted = false;
	
	/**
	 * Construct a new JWebServer with default values.
	 */
	public JWebServer() {
		this.port = DEFAULT_PORT;
		this.webRoot = DEFAULT_WEB_ROOT;
	}
	
	/**
	 * Construct a new JWebServer with a custom web root (and default port).
	 * @param webRoot The root directory of the server (relative or absolute path).
	 */
	public JWebServer(String webRoot) {
		this.port = DEFAULT_PORT;
		this.webRoot = webRoot;
	}
	
	/**
	 * Construct a new JWebServer with a custom port (and default web root).
	 * @param port The port of the server.
	 */
	public JWebServer(int port) {
		this.port = port;
		this.webRoot = DEFAULT_WEB_ROOT;
	}
	
	/**
	 * Construct a new JWebServer with a custom port and web root.
	 * @param port The port of the server.
	 * @param webRoot The root directory of the server (relative or absolute path).
	 */
	public JWebServer(int port, String webRoot) {
		this.port = port;
		this.webRoot = webRoot;
	}
	
	/**
	 * Tell the server to start listening for incoming HTTP requests.
	 * Requests will be served according to this server's Routes.
	 */
	public void start() {
		// Add a default route if none exist
		if (routes.size() == 0) {
			routes.add(new Route(GET, "/", "/index.jweb", new SimpleRenderRouteHandler()));
		}
		
		try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getLoopbackAddress())) {			
			System.out.println("Listening on "
					+ serverSocket.getInetAddress().getHostAddress()
					+ ":" + serverSocket.getLocalPort() + " for incoming connections.");
			// Find the various applications this server can serve (as .jar files in the webRoot)
			File fWebRoot = new File(webRoot);
			File[] jars = fWebRoot.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.toString().endsWith(".jar");
				}
			});
			// Spawn new threads to handle clients
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Incoming connection received: "
						+ clientSocket.getInetAddress().getHostAddress()
						+ ":" + clientSocket.getPort());
				new JWebServerThread(clientSocket, jars, STATIC_CONTENT_DIR).start();
			}
		} catch (IOException e) {
			System.err.println("Unable to open socket on port " + port);
			e.printStackTrace();
		} finally {
			hasStarted = false;
			System.out.println("Shutting down...");
		}
	}
	
	/**
	 * Register a route to this server.
	 * @param route The route to be registered.
	 * @return true if the route could be registered, else false.
	 * @throws IllegalStateException If a route was tried to be registered after the server has started.
	 */
	public boolean registerRoute(Route route) {
		if (hasStarted) throw new IllegalStateException("Routes cannot be modified once server has started.");
		return routes.add(route);
	}
	
	/**
	 * Unregister a route from this server.
	 * @param route The route to be unregistered (must be equal in path, method, and resource).
	 * @return true if the route could be removed, else false.
	 * @throws IllegalStateException If a route was tried to be unregistered after the server has started.
	 */
	public boolean unregisterRoute(Route route) {
		if (hasStarted) throw new IllegalStateException("Routes cannot be modified once server has started.");
		return routes.remove(route);
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		if (hasStarted) throw new IllegalStateException("Web root cannot be changed once server has started.");
		this.webRoot = webRoot;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) throws IllegalStateException {
		if (hasStarted) throw new IllegalStateException("Port cannot be changed once server has started.");
		this.port = port;
	}
}
