package org.gtc.test;

import java.io.IOException;
import java.lang.reflect.Method;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.compiler.Instance;
import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;

public class TestRunner {

	private SourceCode testCode;

	public TestRunner(SourceCode testCode) {
		this.testCode = testCode;
	}

	public TestResult runTest(SourceCode... codes) throws IOException, CompilerException {
		TestResult result = new TestResult();
		result.setName(this.testCode.getName());

		// Prepare a set of test
		SourceCode[] testSet = new SourceCode[codes.length + 1];
		testSet[0] = this.testCode;
		System.arraycopy(codes, 0, testSet, 1, codes.length);

		Compiler compiler = new Compiler(testSet);
		compiler.enableAssertions();
		compiler.compile();

		ClassWrapper testClass = compiler.getClasses().get(this.testCode.getName());
		Instance instance = testClass.newInstance();

		Method testMethod = null;
		try {
			Method[] testMethods = testClass.getTestMethods();
			for (int i = 0; i < testMethods.length; i++) {
				testMethod = testMethods[i];
				instance.call(testMethod.getName());
				result.addResult(testMethod.getName(), true);
			}
		} catch (AssertionError e) {
			result.addResult(testMethod.getName(), false);
		} catch (Exception e) {
			throw new RuntimeException("unexpected error", e);
		}

		result.setElapsedTime(Util.nanoSeconds(2));

		return result;
	}

}
