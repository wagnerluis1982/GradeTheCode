package org.gtc.compiler;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.gtc.sourcecode.ClassNotDefinedException;
import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;
import org.junit.Test;

public class CompilerTest {

	private static String simpleCode = "package com.test;" +
			"public class Test { public int getNumber() { return 1000; } }";

	@Test
	public void testCompile() throws IOException, CompilerException,
			ClassNotDefinedException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		Compiler compiler = new Compiler(new SourceCode(simpleCode));

		Map<String, ClassWrapper> classes = compiler.compile();
		assertEquals(1, classes.size());

		ClassWrapper cw = classes.get("com.test.Test");
		assertNotNull(cw);
		assertEquals("com.test.Test", cw.getName());

		Instance instance = cw.newInstance();
		assertEquals(1000, instance.call("getNumber"));
	}

	@Test
	public void testCompileInformingTarget() throws IOException, CompilerException, ClassNotDefinedException {
		File targetDir = Util.createTempDir();
		targetDir.deleteOnExit();

		Compiler compiler = new Compiler(targetDir, new SourceCode(simpleCode));
		compiler.compile();

		assertTrue(targetDir.exists());
	}

	@Test
	public void testCompileOut() throws ClassNotDefinedException, IOException, DuplicatedCodeException {
		Compiler compiler = new Compiler(new SourceCode(simpleCode + "invalidToken"));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			compiler.compile(new PrintStream(out));
		} catch (CompilerException e) {}

		assertTrue(out.size() + " isn't greater than 0", out.size() > 0);
	}

}
