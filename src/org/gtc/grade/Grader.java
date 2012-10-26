package org.gtc.grade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.gtc.compiler.ClassWrapper;
import org.gtc.test.Conformity;
import org.gtc.test.ConformityResult;
import org.gtc.test.ConformityRules;
import org.gtc.test.TestMethodResult;
import org.gtc.test.TestResult;
import org.gtc.test.TestRunner;
import org.gtc.test.TestStatus;
import org.gtc.util.Util;

public class Grader {

	private Conformity conformity;
	private TreeMap<String,TestResult> basisResults;

	public Grader(ConformityRules conformityRules, Iterable<TestResult> basisResults) {
		conformity = new Conformity(conformityRules);

		this.basisResults = new TreeMap<String, TestResult>();
		for (TestResult testResult : basisResults)
			this.basisResults.put(testResult.getName(), testResult);
	}

	public GradeResult getGrade(String name, Map<String, ClassWrapper> checkingClasses, ClassWrapper[] assertionClasses) {
		ConformityResult conformityResult = conformity.check(checkingClasses);
		if (conformityResult.iterator().hasNext())
			return new GradeResult(name, 0, "FAIL: didn't pass in the conformity check");

		List<Double> grades = new ArrayList<Double>();

		TestRunner testRunner = new TestRunner();
		for (ClassWrapper testClass : assertionClasses) {
			TestResult checkingResult = testRunner.runTest(testClass);
			TestResult basisResult = basisResults.get(checkingResult.getName());

			Set<Entry<String, TestMethodResult>> methodResultEntries = checkingResult.getMethodResults().entrySet();
			for (Entry<String, TestMethodResult> resultEntry : methodResultEntries) {
				TestMethodResult methodResult = resultEntry.getValue();
				if (methodResult.getStatus() != TestStatus.SUCCESS) {
					grades.add(0d);
					continue;
				} else {
					double baseTime = basisResult.getMethodResults().get(resultEntry.getKey()).getElapsedTime();
					double checkingTime = methodResult.getElapsedTime().doubleValue();
					grades.add((baseTime / checkingTime) * 100);
				}
			}
		}

		return new GradeResult(name, Util.average(grades.toArray(new Double[0])), "SUCCESS");
	}

}
