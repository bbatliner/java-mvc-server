package controllers;

import bbatliner.http.HttpRequest;
import bbatliner.http.HttpResponse;
import bbatliner.jweb.Controller;

public class HomeController extends Controller {
	public HomeController(HttpRequest request, ClassLoader loader) {
		super(request, loader);
	}
	public HttpResponse index() {
		return View(null);
	}
	public HttpResponse create() {
		return View(null);
	}
}
