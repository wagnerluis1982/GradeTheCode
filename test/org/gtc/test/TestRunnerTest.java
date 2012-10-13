package org.gtc.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.sourcecode.ClassNotDefinedException;
import org.gtc.sourcecode.EmptyCodeException;
import org.gtc.sourcecode.SourceCode;
import org.junit.Test;

public class TestRunnerTest {

	@Test
	public void test() throws EmptyCodeException, ClassNotDefinedException, IOException, CompilerException {
		fail("Not yet implemented");

		Compiler compiler = new Compiler(
				new SourceCode(resource("examples/Ideal.src")),
				new SourceCode(resource("examples/TestCode.src"))
			);
		Map<String, ClassWrapper> classes = compiler.compile();

		TestRunner runner = new TestRunner();
	}

	private InputStream resource(String name) {
		return getClass().getResourceAsStream(name);
	}

}
