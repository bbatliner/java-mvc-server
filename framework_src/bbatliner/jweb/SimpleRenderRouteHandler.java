package bbatliner.jweb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import bbatliner.http.HttpRequest;
import bbatliner.http.HttpResponse;
import bbatliner.http.Route;
import bbatliner.http.RouteHandler;

/**
 * A simple RouteHandler that just renders the resource as-is,
 * without any templating or additional functionality.
 * @author Brendan Batliner
 *
 */
public class SimpleRenderRouteHandler implements RouteHandler {

	@Override
	public HttpResponse handle(Route route, HttpRequest request, PrintWriter out) throws RenderException, IOException {
		HttpResponse response = new HttpResponse();
		response.setCode(200);
		response.setMessage("OK");
//		// Render .jweb and serve anything else as-is
//		Path resource = Paths.get(route.getResource());
//		FileInputStream file = new FileInputStream(resource.toFile());
//		if (route.getResource().substring(route.getResource().lastIndexOf(".")).equals(".jweb")) {
//			ByteArrayOutputStream rendered = new ByteArrayOutputStream();
//			CottinghamJWebPage.render(file, new PrintStream(rendered), request);
//			response.setContent(new ByteArrayInputStream(rendered.toByteArray()));
//		} else {
//			response.setContent(file);
//		}
		return response;
	}

}
