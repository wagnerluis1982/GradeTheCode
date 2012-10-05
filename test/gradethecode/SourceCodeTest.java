package gradethecode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class SourceCodeTest {

	private static String simpleCode = "\n 	" +
			"package com.teste;\n\n" +
			"public class Teste {\n" +
			"	public int number() {\n" +
			"		return 1000;\n" +
			"	}\n" +
			"}\n";

	private static File tempFile;

	@BeforeClass
	public static void setUpClass() throws IOException {
		tempFile = File.createTempFile("gtc_", null);
		FileWriter writer = new FileWriter(tempFile);
		writer.write(simpleCode);
		writer.close();

		tempFile.deleteOnExit();
	}

	@Test
	public void testFromString() {
		SourceCode sourceCode = new SourceCode(simpleCode);

		assertEquals(simpleCode, sourceCode.toString());
		assertNotNull(sourceCode.getFile());
	}

	@Test
	public void testFromInputStream() {
		InputStream stream = new ByteArrayInputStream(simpleCode.getBytes());
		SourceCode sourceCode = new SourceCode(stream);

		assertEquals(simpleCode, sourceCode.toString());
		assertNotNull(sourceCode.getFile());
	}

	@Test
	public void testFromFile() {
		SourceCode sourceCode = new SourceCode(tempFile);

		assertEquals(simpleCode, sourceCode.toString());
		assertEquals(tempFile, sourceCode.getFile());
	}

	@Test
	public void testIdentifyPackage() {
		SourceCode sourceCode = new SourceCode(simpleCode);
		assertEquals("com.teste", sourceCode.getPackageName());

		sourceCode = new SourceCode("// Comment\n// Comment\n" + simpleCode);
		assertEquals("com.teste", sourceCode.getPackageName());

		sourceCode = new SourceCode("/* Comment\n   Comment*/\n\n" +
				"/* Comment\n   Comment*/\n\n" + simpleCode);
		assertEquals("com.teste", sourceCode.getPackageName());

		sourceCode = new SourceCode(
				"package/*comment*/com   .   \nteste    \n;\n" +
				"public class Teste {\n" +
				"	public int number() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"}\n");
		assertEquals("com.teste", sourceCode.getPackageName());
	}

	@Test
	public void testNoPackage() {
		SourceCode sourceCode = new SourceCode("  " +
				"public class Teste {\n" +
				"	public int number() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"}\n");

		assertEquals("", sourceCode.getPackageName());
	}

}
