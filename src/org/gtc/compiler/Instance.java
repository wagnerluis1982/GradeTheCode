package org.gtc.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper around any Java object, representing an instance with an easier way
 * to invoke the wrapped instance methods.
 *
 * <p> Example of use:
 * <pre>
 *     Object obj = new Object();
 *     Instance instance = new Instance(obj);
 *
 *     // Helper to invoke underlying methods
 *     instance.call("getClass");
 * </pre>
 *
 * @author Wagner Macedo
 */
public class Instance {

	private Class<?> javaClass;
	private Object actualInstance;

	/**
	 * Create an {@code Instance} object using an actual instance as argument
	 *
	 * @param actualInstance any object
	 */
	protected Instance(Object actualInstance) {
		this.actualInstance = actualInstance;
		this.javaClass = actualInstance.getClass();
	}

	/**
	 * Helper to call the wrapped instance methods.
	 *
	 * @param name the method name
	 * @param args array of arguments to call the method
	 * @return any object returned by the method call
	 *
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws CallMethodException
	 * @see {@link java.lang.reflect.Method#invoke(Object, Object...)}
	 */
	public Object call(String name, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		// obtain parameter types
		Class<?>[] parameterTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			parameterTypes[i] = args[i].getClass();

		Method method = this.javaClass.getDeclaredMethod(name, parameterTypes);
		return this.call(method, args);
	}

	/**
	 * Helper to call the wrapped instance methods.
	 *
	 * @param method the method object, if not member of this class throw an
	 * exception
	 * @param args array of args arguments to call the method
	 * @return any object returned by the method call
	 *
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws CallMethodException
	 */
	public Object call(Method method, Object... args)
			throws IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		// try to invoke method
		try {
			return method.invoke(this.actualInstance, args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof AssertionError)
				throw (AssertionError) e.getCause();

			throw new CallMethodException("unexpected error", e);
		}
	}

	/**
	 * The wrapped instance
	 *
	 * @return the object passed to {@code Instance} constructor
	 */
	public Object getActualInstance() {
		return actualInstance;
	}

	/**
	 * The wrapped instance class
	 *
	 * @return a {@code Class} object
	 */
	public Class<?> getJavaClass() {
		return this.javaClass;
	}

}
