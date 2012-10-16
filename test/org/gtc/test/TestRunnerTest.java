package org.gtc.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;

import japa.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;

import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;
import org.junit.Test;

public class TestRunnerTest {

	@Test
	public void testTestResult() throws IOException, ParseException, CompilerException {
		Compiler compiler = new Compiler(new SourceCode(resource("examples/TestCode.src")));
		compiler.compile();

		TestRunner runner = new TestRunner(new SourceCode(resource("examples/TestCode.src")));
		TestResult result = runner.runTest(new SourceCode(resource("examples/Ideal.src")));

		assertThat(result.getName(), equalTo("com.example.CalculatorTest"));
		assertThat(result.getElapsedTime(), greaterThanOrEqualTo(Util.nanoSeconds(2)));
	}

	private InputStream resource(String name) {
		return getClass().getResourceAsStream(name);
	}

}
