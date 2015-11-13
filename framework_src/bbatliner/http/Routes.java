package bbatliner.http;

import java.util.ArrayList;

/**
 * A wrapper around a List of Routes with additional utility methods.
 * @author Brendan Batliner
 *
 */
public class Routes extends ArrayList<Route> {
	
	/**
	 * Find Route(s) by their path.
	 * @param path The path to search for. 
	 * @return Route(s) which match the provided path.
	 */
	public Routes findByPath(String path) {
		Routes matches = new Routes();
		for (Route route : this) {
			if (route.getPath().equals(path)) {
				matches.add(route);
			}
		}
		return matches;
	}
	
	/**
	 * Find Route(s) by their method.
	 * @param method The method to search for. 
	 * @return Route(s) which match the provided method.
	 */
	public Routes findByMethod(String method) {
		Routes matches = new Routes();
		for (Route route : this) {
			if (route.getMethod().equals(method)) {
				matches.add(route);
			}
		}
		return matches;
	}
	
	/**
	 * Find a Route by its method and path.
	 * @param method The method to search for.
	 * @param path The path to search for. 
	 * @return A Route which matches the provided method and path (could be null).
	 */
	public Route findByMethodAndPath(String method, String path) {
		for (Route route : this) {
			if (route.getMethod().equals(method) && route.getPath().equals(path)) {
				return route;
			}
		}
		return null;
	}
	
	@Override
	public boolean add(Route route) {
		if (findByMethodAndPath(route.getMethod(), route.getPath()) == null) {			
			return super.add(route);
		}			
		else {
			return false;
		}
	}

}
