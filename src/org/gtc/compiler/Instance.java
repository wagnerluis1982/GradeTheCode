package org.gtc.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Instance {

	private Class<?> javaClass;
	private Object actualInstance;

	protected Instance(Class<?> javaClass, Object realInstance) {
		this.javaClass = javaClass;
		this.actualInstance = realInstance;
	}

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

	public Object call(Method method, Object... args)
			throws IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		// try to invoke method
		try {
			return method.invoke(this.actualInstance, args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof AssertionError)
				throw (AssertionError) e.getCause();

			throw new CallMethodException("unexpected error");
		}
	}

	public Object getActualInstance() {
		return actualInstance;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

}
