package org.gtc.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.gtc.test.Conformity.Reason;
import org.gtc.test.MethodRule;

public class ConformityResult implements Iterable<ConformityResult.ClassConformityResult> {

	private Map<String, ClassConformityResult> classes;

	public ConformityResult() {
		classes = new Hashtable<String, ClassConformityResult>();
	}

	public void addMissingClass(String name) {
		ClassConformityResult ccr = this.get(name);
		ccr.markMissing();
	}

	public void addMissingMethod(MethodRule methodRule) {
		ClassConformityResult ccr = this.get(methodRule.getDeclaringClass().getName());
		ccr.addMissingMethod(methodRule);
	}

	public void addNonConformingMethod(MethodRule methodRule,
			Method checkingethod, Reason reason) {
		ClassConformityResult ccr = this.get(methodRule.getDeclaringClass().getName());
		ccr.addNonConformingMethod(methodRule, checkingethod, reason);
	}

	private ClassConformityResult get(String key) {
		ClassConformityResult ccr = classes.get(key);
		if (ccr != null)
			return ccr;

		ccr = new ClassConformityResult(key);
		classes.put(key, ccr);

		return ccr;
	}

	@Override
	public Iterator<ClassConformityResult> iterator() {
		return Collections.unmodifiableCollection(classes.values()).iterator();
	}


	public class ClassConformityResult implements Iterable<MethodConformityResult> {
		private String name;
		private Map<String, MethodConformityResult> methods;
		private boolean missing = false;

		private ClassConformityResult(String name) {
			this.name = name;
			this.methods = new Hashtable<String, MethodConformityResult>();
		}

		private void addMissingMethod(MethodRule methodRule) {
			MethodConformityResult mcr = this.get(methodRule);
			mcr.markMissing();
		}

		private void markMissing() {
			this.missing = true;
		}

		private void addNonConformingMethod(MethodRule methodRule,
				Method checkingethod, Reason reason) {
			MethodConformityResult mcr = this.get(methodRule);
			mcr.markNonConforming(checkingethod, reason);
		}

		public boolean isMissing() {
			return this.missing;
		}

		public String getName() {
			return this.name;
		}

		private MethodConformityResult get(MethodRule mr) {
			String key = String.format("%s->%s(%s)",
					mr.getDeclaringClass().getName(), mr.getName(),
					Arrays.toString(mr.getParameterTypes()));

			MethodConformityResult mcr = methods.get(key);
			if (mcr != null)
				return mcr;

			mcr = new MethodConformityResult(mr);
			methods.put(key, mcr);

			return mcr;
		}

		@Override
		public Iterator<MethodConformityResult> iterator() {
			return Collections.unmodifiableCollection(methods.values()).iterator();
		}
	}

	public class MethodConformityResult {
		private String name;
		private MethodRule methodRule;
		private boolean missing = false;
		private Reason nonConformingReason;
		private Method checkingethod;

		private MethodConformityResult(MethodRule mr) {
			this.name = mr.getName();
			this.methodRule = mr;
		}

		private void markNonConforming(Method checkingethod, Reason reason) {
			this.checkingethod = checkingethod;
			this.nonConformingReason = reason;
		}

		private void markMissing() {
			this.missing = true;
		}

		public String getName() {
			return this.name;
		}

		public MethodRule getMethodRule() {
			return methodRule;
		}

		public boolean isMissing() {
			return this.missing;
		}

		public Reason getNonConformingReason() {
			return nonConformingReason;
		}

		public boolean isNonConforming() {
			return this.nonConformingReason != null;
		}

		public Method getCheckingethod() {
			return checkingethod;
		}
	}

}
