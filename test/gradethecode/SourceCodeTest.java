package gradethecode;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
		SourceCode sc = new SourceCode(simpleCode);

		assertSame(simpleCode, sc.toString());
	}

	@Test
	public void testFromFile() {
		SourceCode sc = new SourceCode(tempFile);

		assertEquals(simpleCode, sc.toString());
	}

	@Test
	public void testIdentifyPackage() {
		SourceCode sc = new SourceCode(simpleCode);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode(tempFile);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("// Comment\n// Comment\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());

		sc = new SourceCode("/* Comment\n   Comment*/\n\n" +
				"/* Comment\n   Comment*/\n\n" + simpleCode);
		assertEquals("com.teste", sc.getPackageName());
	}

	@Test
	public void testIdentifyClass() {
		SourceCode sc = new SourceCode(simpleCode);
		assertEquals("Teste", sc.getClassName());
	}

	
	@Test
	public void testNoPackage() {
		SourceCode sc = new SourceCode("  " +
				"public class Teste {\n" +
				"	public int number() {\n" +
				"		return 1000;\n" +
				"	}\n" +
				"}\n");

		assertEquals("", sc.getPackageName());
	}

	@Test
	public void testSourceCodeObject() throws IOException {
		SourceCode sc = new SourceCode(simpleCode);

		assertEquals("com.teste.Teste", sc.getSourceCodeObject().toString());
		assertSame(sc.getSourceCodeObject().getCharContent(false), simpleCode);
	}

}
