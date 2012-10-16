package org.gtc.compiler;

import static org.junit.Assert.*;

import japa.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;
import org.junit.Test;

public class CompilerTest {

	private static String simpleCode = "package com.test;" +
			"public class Test { public int getNumber() { return 1000; } }";

	@Test
	public void testCompile() throws IOException, ParseException,
			CompilerException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			CallMethodException, ClassNotFoundException {
		Compiler compiler = new Compiler(new SourceCode(simpleCode));
		compiler.compile();

		Map<String, ClassWrapper> classes = compiler.getClasses();
		assertEquals(1, classes.size());

		ClassWrapper cw = classes.get("com.test.Test");
		assertNotNull(cw);
		assertEquals("com.test.Test", cw.getName());

		Instance instance = cw.newInstance();
		assertEquals(1000, instance.call("getNumber"));
	}

	@Test
	public void testCompileInformingTarget() throws ParseException, CompilerException, ClassNotFoundException {
		File targetDir = Util.createTempDir();
		targetDir.deleteOnExit();

		Compiler compiler = new Compiler(targetDir, new SourceCode(simpleCode));
		compiler.compile();

		assertTrue(targetDir.exists());
	}

	@Test(expected=AssertionError.class)
	public void testEnableAssertions() throws ParseException,
			CompilerException, IOException, NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, CallMethodException, ClassNotFoundException {
		SourceCode code = new SourceCode("package test;" +
				"public class Test {" +
					"public void testError() {" +
						"assert false;" +
					"}" +
				"}");

		Compiler compiler = new Compiler(code);
		compiler.enableAssertions();
		compiler.compile();

		ClassWrapper cw = compiler.getClasses().get("test.Test");
		cw.newInstance().call("testError");
	}

}
