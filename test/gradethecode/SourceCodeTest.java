package gradethecode;

import static org.junit.Assert.*;

import gradethecode.exceptions.SourceCodeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class SourceCodeTest {

	private String simpleCode = "\n 	" +
			"package com.teste;\n\n" +
			"public class Teste {\n" +
			"	public int number() {\n" +
			"		return 1000;\n" +
			"	}\n" +
			"}\n";

	@Test
	public void testFromString() throws SourceCodeException, IOException {
		SourceCode sc = new SourceCode(simpleCode);

		assertSame(simpleCode, sc.toString());
		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
	}

	@Test
	public void testFromInputStream() throws SourceCodeException, IOException {
		InputStream stream = new ByteArrayInputStream(simpleCode.getBytes());
		SourceCode sc = new SourceCode(stream);

		assertEquals(simpleCode, sc.toString());
		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
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
		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
	}

	private void testIdentifyPackage(SourceCode sc) throws SourceCodeException {
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("// Comment\n// Comment\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("/* Comment\n   Comment*/\n\n" +
				"/* Comment\n   Comment*/\n\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());
	}

	private void testIdentifyClass(SourceCode sc) throws SourceCodeException {
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

	private void testJavaFileObject(SourceCode sc) throws IOException, SourceCodeException {
		assertNotNull(sc.getJavaFileObject());
		assertEquals(sc.getJavaFileObject().getCharContent(false), simpleCode);
	}

}
