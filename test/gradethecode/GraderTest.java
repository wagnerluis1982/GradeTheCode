package gradethecode;

import static org.junit.Assert.*;

import java.io.IOException;

import gradethecode.exceptions.CompilerException;
import gradethecode.measure.MeasurementResults;
import gradethecode.measure.MeasurerTest;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GraderTest extends MeasurerTest {

	private static MeasurementResults idealResults;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException {
		MeasurerTest.setUpClass();

		// Load results from Ideal.src
		try {
			idealResults = measurer.measure(new SourceCode(resource("Ideal.src")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGrade() {
		Grader grader = new Grader(idealResults);
	}

	@Ignore("duplicated test")
	@Test
	public void testMeasure() {}

}
