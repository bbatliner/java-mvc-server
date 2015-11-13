package bbatliner.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import bbatliner.jweb.Parameters;

public class ObjectMarshaller {
	// Private constructor (prevents instantiation)
	private ObjectMarshaller() {
		
	}
	
	/**
	 * Constructs a POJO given its Class and any constructor parameters.
	 * @param clazz The class object of the POJO.
	 * @param params Any parameters to be passed to the constructor.
	 * @return A constructed object.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokePojoConstructor(Class<?> clazz, Parameters params) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		// If there is more than one constructor on the POJO, throw it out
		if (constructors.length > 1) {
			throw new InstantiationException();
		}
		Constructor<?> constructor = constructors[0];
		List<Object> constructorParams = new ArrayList<>();
		for (Parameter par : constructor.getParameters()) {
			Object param = params.get(par.getName());
			constructorParams.add(ObjectCaster.toType(param, par.getType()));
		}
		return constructor.newInstance(constructorParams.toArray());
	}
	
	/**
	 * Invoke a method on a given object.
	 * @param o The object on which to invoke the method.
	 * @param methodName The name of the method to invoke.
	 * @param params Any parameters that should be parsed and passed to the method.
	 * @return An Object representing the return result of the method (can be null).
	 * @throws NoSuchMethodException If the named method does not exist on the object.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	public static Object invokeMethod(Object o, String methodName, Parameters params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException, InstantiationException {
		// Find the method on the object
		Class<?> clazz = o.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		Method method = null;
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				method = m;
			}
		}
		if (method == null) throw new NoSuchMethodException();
		
		// Parse any parameters to this method
		List<Object> paramValues = new ArrayList<>();
		for (Parameter p : method.getParameters()) {
			Object actualParam = null;
			Class<?> actualType = p.getType();
			boolean isSimple = actualType.isPrimitive() || actualType.equals(String.class);
			if (isSimple) {
				// Parse primitive and String types using simple conversion
				String rawParam = params.get(p.getName());
				actualParam = ObjectCaster.toType(rawParam, actualType);
			} else {
				// Parse complex objects by constructing them
				actualParam = invokePojoConstructor(p.getType(), params);				
			}
			paramValues.add(actualParam);
		}
		
		// Invoke the method and return the result
		return method.invoke(o, paramValues.toArray());
	}
}
