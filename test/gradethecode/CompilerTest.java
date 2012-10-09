package gradethecode;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.DuplicateSourceCodeException;

import org.junit.Test;

public class CompilerTest {

	private static String simpleCode = "package com.teste;" +
			"public class Teste { public int number() { return 1000; } }";

	@Test
	public void testCompile() throws IOException, CompilerException, DuplicateSourceCodeException, ClassNotDefinedException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Compiler compiler = new Compiler();
		compiler.addSourceCode(new SourceCode(simpleCode));
		assertTrue(compiler.compile());

		Map<String, Class<?>> classes = compiler.loadClasses();
		assertEquals(1, classes.size());

		Class<?> c = classes.get("com.teste.Teste");
		assertEquals("com.teste.Teste", c.getName());

		Method m = c.getDeclaredMethod("number");
		assertEquals(1000, m.invoke(c.newInstance()));

		compiler.dispose();
	}

	@Test
	public void testDispose() throws IOException, CompilerException, DuplicateSourceCodeException, ClassNotDefinedException {
		Compiler compiler = new Compiler();
		compiler.addSourceCode(new SourceCode(simpleCode));
		compiler.compile();

		File targetDir = compiler.getTargetDir();
		Map<String, File> paths = new HashMap<String, File>();
		paths.put("com.teste", new File(
				targetDir.getAbsolutePath() + "/com/teste/"));
		paths.put("com.teste.Teste", new File(
				targetDir.getAbsolutePath() + "/com/teste/Teste.class"));

		assertTrue(targetDir.exists());
		assertTrue(paths.get("com.teste").exists());
		assertTrue(paths.get("com.teste.Teste").exists());

		assertTrue(compiler.dispose());
		assertFalse(targetDir.exists());
	}

	@Test
	public void testDisposeInformingTarget() throws IOException,
			CompilerException, DuplicateSourceCodeException,
			ClassNotDefinedException {
		File targetDir = Files.createTempDirectory("gtcTest_").toFile();
		targetDir.deleteOnExit();

		Compiler compiler = new Compiler(targetDir);
		compiler.addSourceCode(new SourceCode(simpleCode));
		compiler.compile();

		Map<String, File> paths = new HashMap<String, File>();
		paths.put("com", new File(
				targetDir.getAbsolutePath() + "/com/"));
		paths.put("com.teste.Teste", new File(
				targetDir.getAbsolutePath() + "/com/teste/Teste.class"));

		assertTrue(targetDir.exists());
		assertTrue(paths.get("com").exists());
		assertTrue(paths.get("com.teste.Teste").exists());

		assertTrue(compiler.dispose());
		assertTrue(targetDir.exists());
		assertFalse(paths.get("com").exists());
	}

}
