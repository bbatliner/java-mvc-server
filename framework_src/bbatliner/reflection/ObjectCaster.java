package bbatliner.reflection;

/**
 * A utility class to cast between primitive types and Strings.
 * @author Brendan Batliner
 *
 */
public final class ObjectCaster {
	// Private constructor (prevents instantiation)
	private ObjectCaster() {
		
	}
	
	public static int stringToInt(String s) {
		return Integer.parseInt(s);
	}
	
	public static Object toType(Object o, Class<?> type) {
		if (type.equals(int.class)) {
			return stringToInt(o.toString());
		} else if (type.equals(String.class)) {
			return o.toString();
		}
		return null;
	}

}
