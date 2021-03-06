package org.gtc.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConformityRules {

	private Set<ClassRules> classRules;

	public ConformityRules(ClassRules... clsRules) {
		this.classRules = new HashSet<ClassRules>(Arrays.asList(clsRules));
	}

	public Set<ClassRules> getClassesRules() {
		return Collections.unmodifiableSet(this.classRules);
	}

}
