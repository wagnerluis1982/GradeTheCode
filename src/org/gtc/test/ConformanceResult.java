package org.gtc.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.gtc.test.MethodRule;

public class ConformanceResult implements Iterable<ConformanceResult.ClassConformanceResult> {

	private Map<String, ClassConformanceResult> classes;

	public ConformanceResult() {
		classes = new Hashtable<String, ClassConformanceResult>();
	}

	private ClassConformanceResult get(String key) {
		ClassConformanceResult ccr = classes.get(key);
		if (ccr != null)
			return ccr;

		ccr = new ClassConformanceResult(key);
		classes.put(key, ccr);

		return ccr;
	}

	public void addMissingClass(String name) {
		ClassConformanceResult ccr = this.get(name);
		ccr.markMissing();
	}

	public void addMissingMethod(MethodRule methodRule) {
		ClassConformanceResult ccr = this.get(methodRule.getDeclaringClass().getName());
		ccr.addMissingMethod(methodRule);
	}

	public void setMethodVisibilty(MethodRule methodRule, Visibility visibility) {
		ClassConformanceResult ccr = this.get(methodRule.getDeclaringClass().getName());
		ccr.setMethodVisibility(methodRule, visibility);
	}

	public void setMethodReturnTypeConformity(MethodRule basisMethodRule,
			MethodRule checkingMethodRule) {
		ClassConformanceResult ccr = this.get(basisMethodRule.getDeclaringClass().getName());
		ccr.setMethodReturnTypeConforming(basisMethodRule, checkingMethodRule);
	}

	@Override
	public Iterator<ClassConformanceResult> iterator() {
		return Collections.unmodifiableCollection(classes.values()).iterator();
	}


	public class ClassConformanceResult implements Iterable<MethodConformanceResult> {
		private boolean missing = false;
		private String name;
		private Map<String, MethodConformanceResult> methods;

		private ClassConformanceResult(String name) {
			this.name = name;
			this.methods = new Hashtable<String, MethodConformanceResult>();
		}

		public void setMethodReturnTypeConforming(MethodRule basis, MethodRule checking) {
			MethodConformanceResult mcr = this.get(basis);
			mcr.setReturnTypeConforming(basis.getReturnType().equals(checking.getReturnType()));
		}

		private MethodConformanceResult get(MethodRule mr) {
			String key = String.format("%s->%s(%s)",
					mr.getDeclaringClass().getName(), mr.getName(),
					Arrays.toString(mr.getParameterTypes()));

			MethodConformanceResult mcr = methods.get(key);
			if (mcr != null)
				return mcr;

			mcr = new MethodConformanceResult(mr.getName());
			methods.put(key, mcr);

			return mcr;
		}

		public boolean isMissing() {
			return this.missing;
		}

		public String getName() {
			return this.name;
		}

		private void setMethodVisibility(MethodRule methodRule, Visibility v) {
			MethodConformanceResult mcr = this.get(methodRule);
			mcr.setVisibility(v);
		}

		private void markMissing() {
			this.missing = true;
		}

		private void addMissingMethod(MethodRule methodRule) {
			MethodConformanceResult mcr = this.get(methodRule);
			mcr.markMissing();
		}

		@Override
		public Iterator<MethodConformanceResult> iterator() {
			return Collections.unmodifiableCollection(methods.values()).iterator();
		}
	}

	public class MethodConformanceResult {
		private boolean missing = false;
		private String name;
		private Visibility visibility;
		private boolean returnTypeConforming;

		private MethodConformanceResult(String name) {
			this.name = name;
		}

		private void setReturnTypeConforming(boolean conformity) {
			this.returnTypeConforming = conformity;
		}

		private void setVisibility(Visibility v) {
			this.visibility = v;
		}

		public boolean isReturnTypeConforming() {
			return returnTypeConforming;
		}

		public Visibility getVisibility() {
			return visibility;
		}

		public String getName() {
			return this.name;
		}

		public boolean isMissing() {
			return this.missing;
		}

		private void markMissing() {
			this.missing = true;
		}
	}



}
