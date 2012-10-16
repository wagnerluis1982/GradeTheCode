package org.gtc.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassWrapper {

	private final Class<?> javaClass;
	private Method[] declaredMethods;
	private Method[] publicMethods;
	private Method[] testMethods;

	public ClassWrapper(Class<?> javaClass) {
		this.javaClass = javaClass;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public String getName() {
		return this.javaClass.getName();
	}

	public Method[] getDeclaredMethods() {
		this.declaredMethods = this.declaredMethods != null ? this.declaredMethods
				: this.javaClass.getDeclaredMethods();

		return declaredMethods;
	}

	public Method[] getDeclaredPublicMethods() {
		if (this.publicMethods != null)
			return this.publicMethods;

		List<Method> methods = new ArrayList<Method>();

		for (Method method : this.getDeclaredMethods())
			if (Modifier.isPublic(method.getModifiers()))
				methods.add(method);

		return (this.publicMethods = methods.toArray(new Method[0]));
	}

	public Method[] getTestMethods() {
		if (this.testMethods != null)
			return this.testMethods;

		List<Method> methods = new ArrayList<Method>();

		for (Method method : this.getDeclaredPublicMethods())
			if (method.getName().startsWith("test")
					&& method.getParameterTypes().length == 0)
				methods.add(method);

		return (this.testMethods = methods.toArray(new Method[0]));
	}

	public Instance newInstance() {
		try {
			return new Instance(this.javaClass, this.javaClass.newInstance());
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return null;
	}

}
