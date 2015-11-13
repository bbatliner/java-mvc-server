package bbatliner.jweb;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A wrapper class to hold HTTP parameters (query strings or otherwise).
 * @author Brendan Batliner
 *
 */
public class Parameters extends LinkedHashMap<String, String> {
	
	private final static String ENCODING = "UTF-8";
	
	public static Parameters parseQueryString(String queryString) throws UnsupportedEncodingException {
		Parameters p = new Parameters();
		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			int i = pair.indexOf('=');
			p.put(URLDecoder.decode(pair.substring(0, i), ENCODING), URLDecoder.decode(pair.substring(i + 1), ENCODING));
		}
		return p;
	}
	
//	public String getValueByType(Class<?> type) {
//		for (Map.Entry<String, String> entry : this.entrySet()) {
//			if (entry.getClass().equals(type)) {
//				return entry.getValue();
//			}
//		}
//		return null;
//	}
}
