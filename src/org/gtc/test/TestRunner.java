package org.gtc.test;

import java.lang.reflect.Method;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Instance;

public class TestRunner {

	public TestResult runTest(ClassWrapper testClass) {
		final TestResult result = new TestResult();
		result.setName(testClass.getName());

		final Method[] testMethods = testClass.getTestMethods();
		final long hardStartTime = System.nanoTime();
		final Instance instance = testClass.newInstance();
		for (Method method : testMethods) {
			try {
				long startTime = System.nanoTime();
				instance.call(method);
				long elapsedTime = System.nanoTime() - startTime;
				result.addMethodResult(method.getName(),
						new TestMethodResult(true, elapsedTime));
			} catch (AssertionError e) {
				result.addMethodResult(method.getName(),
						new TestMethodResult(false, 0));
			} catch (Exception e) {
				throw new RuntimeException("unexpected error", e);
			}
		}
		long hardElapsedTime = System.nanoTime() - hardStartTime;
		result.setElapsedTime(hardElapsedTime);

		return result;
	}

}
