package org.gtc.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.gtc.compiler.ClassWrapper;
import org.gtc.test.MethodRule;

public class ConformanceChecker {

	private ConformanceRules rules;

	public ConformanceChecker(ConformanceRules rules) {
		this.rules = rules;
	}

	public ConformanceResult check(Map<String, ClassWrapper> classes) {
		ConformanceResult result = new ConformanceResult();

		for (ClassRules classRules : this.rules.getClassesRules()) {
			String className = classRules.getName();
			ClassWrapper wrapper = classes.get(className);

			if (wrapper == null) {
				result.addMissingClass(className);
				continue;
			}

			for (MethodRule methodRule : classRules.getMethodRules()) {
				Method method = wrapper.getDeclaredMethod(methodRule.getName(),
						methodRule.getParameterTypes());

				if (method == null) {
					result.addMissingMethod(methodRule);
					continue;
				}

				Visibility visibility = methodVisibility(method);
				result.setMethodVisibilty(methodRule, visibility);

				if (!visibility.equals(Visibility.PUBLIC))
					continue;

				MethodRule checkingMethodRule = new MethodRule(method);
				result.setMethodReturnTypeConformity(methodRule, checkingMethodRule);
			}
		}

		return result;
	}

	private Visibility methodVisibility(Method method) {
		int mod = method.getModifiers();

		if (Modifier.isPrivate(mod))
			return Visibility.PRIVATE;
		if (Modifier.isProtected(mod))
			return Visibility.PROTECTED;
		if (Modifier.isPublic(mod))
			return Visibility.PUBLIC;
		else
			return Visibility.OTHER;
	}

}
