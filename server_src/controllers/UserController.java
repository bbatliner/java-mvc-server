package controllers;

import bbatliner.http.HttpRequest;
import bbatliner.http.HttpResponse;
import bbatliner.jweb.Controller;
import models.User;

public class UserController extends Controller {
	public UserController(HttpRequest request, ClassLoader loader) {
		super(request, loader);
	}
	public HttpResponse index() {
		return View(null);
	}
	public HttpResponse create(User u) {
		return View(u);
	}
}
