package bbatliner.jweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import bbatliner.http.HttpProtocol;

/**
 * A Thread to handle HTTP requests sent to a JWebServer.
 * @author Brendan Batliner
 *
 */
public class JWebServerThread extends Thread {

	private Socket socket;
	private String staticContentDir;
	private File[] applications;
	
	/**
	 * Construct a new JWebServerThread with the provided parameters.
	 * @param socket The client socket that this thread is receiving requests from.	 
	 * @param jars The array of Files holding .jars that this server should serve the applications of.
	 * @param staticContentDir The static content directory this thread should serve static assets from.
	 */
	public JWebServerThread(Socket socket, File[] jars, String staticContentDir) {
		super("WebServerThread");
		this.socket = socket;
		this.applications = jars;
		this.staticContentDir = staticContentDir;
	}
	
	@Override
	public void run() {
		// Get the client socket's IO streams
		try (
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		) {
			HttpProtocol http = new HttpProtocol(applications, staticContentDir);
			http.listen(in, out);
		} catch (SocketException e) {
			System.err.println("Socket error: " + e.getMessage());
			System.err.println("Did the client close the connection?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
