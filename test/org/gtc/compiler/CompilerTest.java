package org.gtc.compiler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.sourcecode.ClassNotDefinedException;
import org.gtc.sourcecode.SourceCode;
import org.junit.Test;

public class CompilerTest {

	private static String simpleCode = "package com.teste;" +
			"public class Teste { public int getNumber() { return 1000; } }";

	@Test
	public void testCompile() throws IOException, CompilerException,
			ClassNotDefinedException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			CallMethodException {
		Compiler compiler = new Compiler();
		compiler.addSourceCode(new SourceCode(simpleCode));

		Map<String, ClassWrapper> classes = compiler.compile();
		assertEquals(1, classes.size());

		ClassWrapper cw = classes.get("com.teste.Teste");
		assertEquals("com.teste.Teste", cw.getName());

		Instance instance = cw.newInstance();
		assertEquals(1000, instance.call("getNumber"));
	}

	@Test
	public void testCompileInformingTarget() throws IOException, CompilerException, ClassNotDefinedException {
		File targetDir = Files.createTempDirectory("gtcTest_").toFile();
		targetDir.deleteOnExit();

		Compiler compiler = new Compiler(targetDir);
		compiler.addSourceCode(new SourceCode(simpleCode));
		compiler.compile();

		assertTrue(targetDir.exists());
	}

}
