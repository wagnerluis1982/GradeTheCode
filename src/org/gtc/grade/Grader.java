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

/**
 * Class used to compute a grade based on conformity rules and a basis test
 * result.
 *
 * @author Wagner Macedo
 */
public class Grader {

	private Conformity conformity;
	private TreeMap<String,TestResult> basisResults;

	/**
	 * Contruct a {@code Grader} object with a conformity rules and basis test.
	 *
	 * @param conformityRules {@link ConformityRules} object
	 * @param basisResults any iterable of {@link TestResult} objects
	 */
	public Grader(ConformityRules conformityRules, Iterable<TestResult> basisResults) {
		conformity = new Conformity(conformityRules);

		this.basisResults = new TreeMap<String, TestResult>();
		for (TestResult testResult : basisResults)
			this.basisResults.put(testResult.getName(), testResult);
	}

	/**
	 * Compute the grade of a set of pre-compiled classes passed in arguments.
	 *
	 * @param name Identify this result. This is normally the student name or
	 * his or her enrollment number.
	 * @param checkingClasses the student's java classes
	 * @param assertionClasses the test classes that test {@code checkingClasses}
	 * @return a {@link GradeResult} object with grading data
	 */
	public GradeResult getGrade(String name, Map<String, ClassWrapper> checkingClasses,
			ClassWrapper[] assertionClasses) {
		ConformityResult conformityResult = conformity.check(checkingClasses);
		if (conformityResult.iterator().hasNext())
			return new GradeResult(name, 0, "FAIL: didn't pass in the conformity check");

		List<Double> grades = new ArrayList<Double>();
		StringBuffer notes = new StringBuffer();

		TestRunner testRunner = new TestRunner();
		for (ClassWrapper testClass : assertionClasses) {
			TestResult checkingResult = testRunner.runTest(testClass);
			TestResult basisResult = basisResults.get(checkingResult.getName());

			Set<Entry<String, TestMethodResult>> methodResultEntries = checkingResult.getMethodResults().entrySet();
			notes.append(testClass.getName()).append("\n");
			for (Entry<String, TestMethodResult> resultEntry : methodResultEntries) {
				TestMethodResult methodResult = resultEntry.getValue();
				TestStatus testStatus = methodResult.getStatus();
				notes.append("- ").append(resultEntry.getKey()).append(" ")
					.append(testStatus);
				if (testStatus != TestStatus.SUCCESS) {
					grades.add(0d);
					continue;
				} else {
					double baseTime = basisResult.getMethodResults().get(resultEntry.getKey()).getElapsedTime();
					long checkingTime = methodResult.getElapsedTime();
					notes.append(" ").append(String.format("%.2f",
							Util.milliSeconds(checkingTime))).append(" ms");
					grades.add((baseTime / checkingTime) * 10);
				}
				notes.append("\n");
			}
		}

		return new GradeResult(name, Util.average(grades.toArray(new Double[0])), notes);
	}

}
