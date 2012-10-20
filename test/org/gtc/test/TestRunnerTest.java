package org.gtc.test;

import static org.junit.Assert.*;
import static org.gtc.util.Util.nanoSeconds;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;

import japa.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.sourcecode.SourceCode;
import org.junit.Test;

public class TestRunnerTest {

	@Test
	public void testTestResult() throws IOException, ParseException, CompilerException {
		Compiler compiler = new Compiler();
		compiler.addAssertionCodes(new SourceCode(resource("examples/TestCode.src")));
		compiler.addCodes(new SourceCode(resource("examples/Ideal.src")));
		compiler.compile();

		TestRunner runner = new TestRunner();
		TestResult result = runner.runTest(compiler.getAssertionClasses()[0]);

		assertThat(result.getName(), equalTo("com.example.CalculatorTest"));
		assertThat(result.getElapsedTime(),
				greaterThanOrEqualTo(nanoSeconds(4)));

		Map<String, TestMethodResult> methodResults = result.getMethodResults();

		TestMethodResult testCurrentNumber = methodResults.get("testCurrentNumber");
		assertEquals(TestStatus.SUCCESS, testCurrentNumber.getStatus());
		assertThat(testCurrentNumber.getElapsedTime(),
				greaterThanOrEqualTo(nanoSeconds(1)));

		TestMethodResult testPlus = methodResults.get("testPlus");
		assertEquals(TestStatus.SUCCESS, testPlus.getStatus());
		assertThat(testPlus.getElapsedTime(),
				greaterThanOrEqualTo(nanoSeconds(2)));
	}

	private InputStream resource(String name) {
		return getClass().getResourceAsStream(name);
	}

}
