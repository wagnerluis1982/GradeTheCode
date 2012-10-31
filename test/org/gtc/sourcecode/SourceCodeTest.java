package org.gtc.sourcecode;

import static org.junit.Assert.*;

import japa.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.gtc.sourcecode.SourceCode;
import org.junit.Ignore;
import org.junit.Test;

public class SourceCodeTest {

	private String simpleCode = "\n 	" +
			"package com.test;\n\n" +
			"import java.util.*;" +
			"public class Test {\n" +
			"	public int getNumber() {\n" +
			"		return 1000;\n" +
			"	}\n" +
			"	private void noUse(String arg, int n) {\n" +
			"	}\n" +
			"	private List<String> noUse() {\n" +
			"		return null;" +
			"	}\n" +
			"}\n";

	@Test
	public void testFromString() throws ParseException, IOException {
		SourceCode sc = new SourceCode(simpleCode);

		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
	}

	@Test
	public void testFromInputStream() throws ParseException, IOException {
		InputStream stream = new ByteArrayInputStream(simpleCode.getBytes());
		SourceCode sc = new SourceCode(stream);

		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
	}

	@Test
	public void testFromFile() throws IOException, ParseException {
		File tempFile = File.createTempFile("gtc_", null);
		tempFile.deleteOnExit();
		FileWriter writer = new FileWriter(tempFile);
		writer.write(simpleCode);
		writer.close();

		SourceCode sc = new SourceCode(tempFile);

		testIdentifyPackage(sc);
		testIdentifyClass(sc);
		testJavaFileObject(sc);
	}

	@Test
	public void testIdentifyClass() throws ParseException {
		SourceCode sc = new SourceCode(
				"package com.test;\n\n" +
				"public class Test {\n" +
				"	public int getNumber() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"	public int getNumberSquared(){\n" +
				"		// class CommentToFool\n" +
				"		return getNumber() * getNumber();\n" +
				"	}\n" +
				"}\n");
		assertEquals("Test", sc.getClassName());
	}

	@Test
	@Ignore
	public void testNoPackage() throws ParseException {
		SourceCode sc = new SourceCode("  " +
				"public class Test {\n" +
				"	public int number() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"}\n");

		assertEquals(null, sc.getPackageName());
		assertEquals("Test", sc.getName());
	}

	private void testIdentifyPackage(SourceCode sc) throws ParseException {
		assertEquals("com.test", sc.getPackageName());

		sc = new SourceCode("// package org.test; \n// Comment\n" + simpleCode);
		assertEquals("com.test", sc.getPackageName());

		sc = new SourceCode("/* Comment\n   Comment*/\n\n" +
				"/* Comment\n   Comment*/\n\n" + simpleCode);
		assertEquals("com.test", sc.getPackageName());
	}

	private void testIdentifyClass(SourceCode sc) {
		assertEquals("Test", sc.getClassName());
	}

	private void testJavaFileObject(SourceCode sc) throws IOException {
		assertNotNull(sc.getJavaFileObject());
		assertEquals(sc.getJavaFileObject().getCharContent(false), sc.getCode());
	}

}
