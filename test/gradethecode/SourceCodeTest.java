package gradethecode;

import static org.junit.Assert.*;

import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.SourceCodeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class SourceCodeTest {

	private static String simpleCode = "\n 	" +
			"package com.teste;\n\n" +
			"public class Teste {\n" +
			"	public int number() {\n" +
			"		return 1000;\n" +
			"	}\n" +
			"}\n";

	@Test
	public void testFromString() throws ClassNotDefinedException {
		SourceCode sc = new SourceCode(simpleCode);

		assertSame(simpleCode, sc.toString());
	}

	@Test
	public void testFromFile() throws SourceCodeException, IOException {
		File tempFile = File.createTempFile("gtc_", null);
		tempFile.deleteOnExit();
		FileWriter writer = new FileWriter(tempFile);
		writer.write(simpleCode);
		writer.close();

		SourceCode sc = new SourceCode(tempFile);

		assertEquals(simpleCode, sc.toString());
	}

	@Test
	public void testIdentifyPackage() throws SourceCodeException {
		SourceCode sc = new SourceCode(simpleCode);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("// Comment\n// Comment\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("/* Comment\n   Comment*/\n\n" +
				"/* Comment\n   Comment*/\n\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());
	}

	@Test
	public void testIdentifyClass() throws SourceCodeException {
		SourceCode sc = new SourceCode(simpleCode);
		assertEquals("Teste", sc.getClassName());
	}

	@Test
	public void testNoPackage() throws SourceCodeException {
		SourceCode sc = new SourceCode("  " +
				"public class Teste {\n" +
				"	public int number() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"}\n");

		assertEquals("", sc.getPackageName());
	}

	@Test
	public void testJavaFileObject() throws IOException, SourceCodeException {
		SourceCode sc = new SourceCode(simpleCode);

		assertSame(sc.getJavaFileObject().getCharContent(false), simpleCode);
		assertNotNull(sc.getJavaFileObject());
	}

}
