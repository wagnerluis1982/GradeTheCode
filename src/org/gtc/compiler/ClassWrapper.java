package org.gtc.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassWrapper {

	private final Class<?> javaClass;

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
		return this.javaClass.getDeclaredMethods();
	}

	public List<Method> getDeclaredPublicMethods() {
		List<Method> publicMethods = new ArrayList<Method>();

		for (Method method : this.getDeclaredMethods())
			if (Modifier.isPublic(method.getModifiers()))
				publicMethods.add(method);

		return Collections.unmodifiableList(publicMethods);
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
