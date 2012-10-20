package org.gtc.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.gtc.test.Conformity.Visibility;

public class ClassRules implements Comparable<ClassRules> {

	private String name;
	private Set<MethodRule> methodRules;

	public ClassRules(String name) {
		this.name = name;
		this.methodRules = new HashSet<MethodRule>();
	}

	public String getName() {
		return name;
	}

	public Set<MethodRule> getMethodRules() {
		return Collections.unmodifiableSet(methodRules);
	}

	public void addMethodRule(Method method) {
		methodRules.add(new MethodRule(method));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;

		ClassRules o = (ClassRules) obj;
		return this.name.equals(o.name) &&
				this.methodRules.equals(o.methodRules);
	}

	@Override
	public int compareTo(ClassRules o) {
		if (this.equals(o))
			return 0;

		return 1;
	}

}

class MethodRule implements Comparable<MethodRule> {

	private String name;
	private Class<?> declaringClass;
	private Class<?>[] parameterTypes;
	private Class<?> returnType;

	protected MethodRule(Method method) {
		this.name = method.getName();
		this.declaringClass = method.getDeclaringClass();
		this.parameterTypes = method.getParameterTypes();
		this.returnType = method.getReturnType();
	}

	public String getName() {
		return name;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;

		MethodRule o = (MethodRule) obj;
		return this.returnType.equals(o.returnType) &&
				Arrays.equals(this.parameterTypes, o.parameterTypes);
	}

	@Override
	public int compareTo(MethodRule o) {
		if (this.equals(o))
			return 0;

		return 1;
	}

	public Visibility getVisibility() {
		return Visibility.PUBLIC;
	}

}

