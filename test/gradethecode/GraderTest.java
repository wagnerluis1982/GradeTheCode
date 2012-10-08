package gradethecode;

import static org.junit.Assert.*;

import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.EmptyCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraderTest {

/*
	private enum Grades {
		BETTER, IDEAL, GOOD, POOR
	}

	private static Compiler compiler;
	private static Map<Grades, InputStream> res;
	private static String qualifiedName;
	private static Class<?> idealClass;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException,
			EmptyCodeException, ClassNotDefinedException,
			ClassNotFoundException {
		compiler = new Compiler();

		res = new TreeMap<Grades, InputStream>();
		res.put(Grades.BETTER, resource("measure/examples/Better.src"));
		res.put(Grades.IDEAL, resource("measure/examples/Ideal.src"));
		res.put(Grades.GOOD, resource("measure/examples/Good.src"));
		res.put(Grades.POOR, resource("measure/examples/Poor.src"));

		compiler.addSourceCode(new SourceCode(res.get(Grades.IDEAL)));
		compiler.compile();

		idealClass = compiler.loadClasses().get(qualifiedName);
		// Measurement area
	}

	@AfterClass
	public static void tearDownClass() {
		compiler.dispose();
	}

	@Before
	public void setUp() {
		compiler.reset();
	}

	@Test
	public void testGrade() {

	}

	private static InputStream resource(String name) {
		return GraderTest.class.getResourceAsStream(name);
	}
*/

}
