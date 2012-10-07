package gradethecode;

import static org.junit.Assert.*;

import gradethecode.exceptions.CompilerException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraderTest {

	private enum Grades {
		BETTER, IDEAL, GOOD, POOR
	}

	private static Compiler compiler;
	private static Map<Grades, InputStream> res;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException,
			URISyntaxException {
		compiler = new Compiler();

		res = new TreeMap<Grades, InputStream>();
		res.put(Grades.BETTER, resource("examples/Better.src"));
		res.put(Grades.IDEAL, resource("examples/Ideal.src"));
		res.put(Grades.GOOD, resource("examples/Good.src"));
		res.put(Grades.POOR, resource("examples/Poor.src"));
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

	private static InputStream resource(String name) throws URISyntaxException {
		return GraderTest.class.getResourceAsStream(name);
	}

}
