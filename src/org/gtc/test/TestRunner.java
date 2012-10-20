package org.gtc.test;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Properties;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Instance;

public class TestRunner {
	private final static SecurityManager SYS_SEC_MAN = System.getSecurityManager();
	private final static SecurityManager SAFE_SEC_MAN = new SecurityManager() {
		private Properties prop = System.getProperties();

		@Override
		public void checkPermission(Permission perm) {
			if (perm instanceof ReflectPermission ||
					new RuntimePermission("setSecurityManager").implies(perm))
				return;

			String classPath = prop.getProperty("java.class.path");
			for (String cp : classPath.split(File.pathSeparator))
				if (new FilePermission(cp, "read").implies(perm))
					return;

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
			TestStatus status = null;
			long elapsedTime = 0;
			try {
				final long startTime = System.nanoTime();
				instance.call(method);
				elapsedTime = System.nanoTime() - startTime;
				status = TestStatus.SUCCESS;
			} catch (AssertionError e) {
				status = TestStatus.FAIL;
			} catch (Exception e) {
				status = TestStatus.ERROR;
			}
			result.addMethodResult(method.getName(),
					new TestMethodResult(status, elapsedTime));
		}
		final long hardElapsedTime = System.nanoTime() - hardStartTime;
		result.setElapsedTime(hardElapsedTime);

		// Restore security to default
		System.setSecurityManager(SYS_SEC_MAN);

		return result;
	}

}
