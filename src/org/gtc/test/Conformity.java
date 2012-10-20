package org.gtc.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.gtc.compiler.ClassWrapper;
import org.gtc.test.MethodRule;

public class Conformity {

	public enum Reason {
		VISIBILITY,
		RETURN_TYPE,
	}

	public enum Visibility {
		PRIVATE,
		PROTECTED,
		PUBLIC,
		OTHER,
	}

	private ConformityRules rules;

	public Conformity(ConformityRules rules) {
		this.rules = rules;
	}

	public ConformityResult check(Map<String, ClassWrapper> classes) {
		ConformityResult result = new ConformityResult();

		for (ClassRules classRules : this.rules.getClassesRules()) {
			String className = classRules.getName();
			ClassWrapper wrapper = classes.get(className);

			if (wrapper == null) {
				result.addMissingClass(className);
				continue;
			}

			for (MethodRule methodRule : classRules.getMethodRules()) {
				Method checkingMethod = wrapper.getDeclaredMethod(methodRule.getName(),
						methodRule.getParameterTypes());

				if (checkingMethod == null) {
					result.addMissingMethod(methodRule);
					continue;
				}

				Visibility visibility = methodVisibility(checkingMethod);
				if (!methodRule.getVisibility().equals(visibility)) {
					result.addNonConformingMethod(methodRule, checkingMethod,
							Reason.VISIBILITY);
					continue;
				}

				Class<?> returnType = checkingMethod.getReturnType();
				if (!methodRule.getReturnType().equals(returnType)) {
					result.addNonConformingMethod(methodRule, checkingMethod,
							Reason.RETURN_TYPE);
				}
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
			// Probably never
			return Visibility.OTHER;
	}

}
