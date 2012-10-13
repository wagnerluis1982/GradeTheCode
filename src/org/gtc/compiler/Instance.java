package org.gtc.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Instance {

	private Class<?> javaClass;
	private Object realInstance;

	protected Instance(Class<?> javaClass, Object realInstance) {
		this.javaClass = javaClass;
		this.realInstance = realInstance;
	}

	public Object call(String name, Object... args)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		// obtain parameter types
		Class<?>[] parameterTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			parameterTypes[i] = args[i].getClass();

		// try to invoke method
		try {
			Method method = this.javaClass.getDeclaredMethod(name, parameterTypes);
			return method.invoke(this.realInstance, args);
		} catch (InvocationTargetException e) {
			throw new CallMethodException("unexpected error");
		}
	}

}
