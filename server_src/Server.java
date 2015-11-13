import bbatliner.jweb.JWebServer;

public class Server {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java -jar ws.jar <port> <web root>");
			return;
		}
		
		int port = Integer.parseInt(args[0]);
		String webRoot = args[1];
		
		JWebServer server = new JWebServer();
		
		server.setPort(port);
//		server.setWebRoot(webRoot);
		
//		server.registerRoute(new Route(GET, "/", "/index.jweb", new SimpleRenderRouteHandler()));
//		server.registerRoute(new Route(POST, "/", "/post_index.jweb", new SimpleRenderRouteHandler()));
//		server.registerRoute(new Route(GET, "/sayHello", "/subdir/hello.jweb", new SimpleRenderRouteHandler()));
		
		server.start();
	}
}
