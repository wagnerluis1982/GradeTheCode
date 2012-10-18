package org.gtc.test;

import java.io.FilePermission;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Instance;

public class TestRunner {
	private final static SecurityManager SYS_SEC_MAN = System.getSecurityManager();
	private final static SecurityManager SAFE_SEC_MAN = new SecurityManager() {
		@Override
		public void checkPermission(Permission perm) {
			if (perm instanceof ReflectPermission ||
				new FilePermission("<<ALL FILES>>", "read").implies(perm) ||
				new RuntimePermission("setSecurityManager").implies(perm))
			{
				return;
			}
			super.checkPermission(perm);
		}
	};

	public TestResult runTest(ClassWrapper testClass) {
		final TestResult result = new TestResult();
		result.setName(testClass.getName());

		final Method[] testMethods = testClass.getTestMethods();
		final long hardStartTime = System.nanoTime();

		// Restrict security to execute external code in a safe way
		System.setSecurityManager(SAFE_SEC_MAN);

		// Execute code
		final Instance instance = testClass.newInstance();
		for (Method method : testMethods) {
			try {
				final long startTime = System.nanoTime();
				instance.call(method);
				final long elapsedTime = System.nanoTime() - startTime;
				result.addMethodResult(method.getName(),
						new TestMethodResult(true, elapsedTime));
			} catch (AssertionError e) {
				result.addMethodResult(method.getName(),
						new TestMethodResult(false, 0));
			} catch (Exception e) {
				throw new RuntimeException("unexpected error", e);
			}
		}
		final long hardElapsedTime = System.nanoTime() - hardStartTime;
		result.setElapsedTime(hardElapsedTime);

		// Restore security to default
		System.setSecurityManager(SYS_SEC_MAN);

		return result;
	}

}
