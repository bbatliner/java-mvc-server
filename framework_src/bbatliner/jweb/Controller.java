package bbatliner.jweb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import bbatliner.http.HttpRequest;
import bbatliner.http.HttpResponse;

public abstract class Controller {

	/**
	 * The request object associated with a specific instance of a controller.
	 */
    protected final HttpRequest request;
    /**
     * The loader responsible for streaming files and resources.
     */
    protected final ClassLoader loader;

    protected Controller(HttpRequest request, ClassLoader loader) {
        this.request = request;
        this.loader = loader;
    }

    /**
     * Render a view with the provided model.<br><br>
     * This method will determine the correct view to render based on the method that calls it.
     * For example, if the `create` method of a Controller calls View(), then the create.jweb
     * file will be rendered from the appropriate views folder.
     * @param model The model to render the view with. Can be null.
     * @return An HttpResponse ready to be sent.
     */
    protected final HttpResponse View(Object model) {
    	HttpResponse response = new HttpResponse();
		response.setCode(200);
		response.setMessage("OK");
		// TODO: Probably not the best way to do this...
		StackTraceElement callingController = new Throwable().getStackTrace()[1];
		String controller = callingController.getFileName()
				.substring(0, callingController.getFileName().indexOf("Controller")).toLowerCase();
		String view = callingController.getMethodName().concat(".jweb");
		String resource = String.join("/", "views", controller, view);
		try {
			InputStream file = loader.getResourceAsStream(resource);
			if (file == null) throw new FileNotFoundException();
			ByteArrayOutputStream rendered = new ByteArrayOutputStream();
			CottinghamJWebPage.render(file, new PrintStream(rendered), model, request);
			response.setContent(new ByteArrayInputStream(rendered.toByteArray()));
		} catch (RenderException|FileNotFoundException|ClassNotFoundException e) {
			response = HttpResponse.RESPONSE_500;
			e.printStackTrace();
		}
		return response;
    }
}
