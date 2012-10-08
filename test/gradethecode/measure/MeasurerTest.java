package gradethecode.measure;

import static org.junit.Assert.*;
import static java.io.File.separator;

import gradethecode.SourceCode;
import gradethecode.Compiler;

import java.io.InputStream;

import org.junit.Test;

public class MeasurerTest {

	@Test
	public void testMeasure() {
		MeasurementParams params = new MeasurementParams();

		String clsName = "com.test.Test";
		ClassParams clsParams = new ClassParams(clsName);

		// Defining "method1"
		MethodParams m1 = new MethodParams("method1", null, int.class);
		m1.addComparingRule(null, 45);

		// Defining "method2"
		MethodParams m2 = new MethodParams("method2",
				new Class[] { int.class }, int.class);
		m2.addComparingRule(new Object[] {10}, 1);
		m2.addComparingRule(new Object[] {-1}, 0);

		// Adding methods to class
		clsParams.addMethodParam(m1);
		clsParams.addMethodParam(m2);

		// Adding ClassParams to MeasurementParams
		params.addClassParams(clsParams);

		Measurer measurer = new Measurer(params);
		Compiler compiler = null;

		try {
			compiler = new Compiler();
			SourceCode code;

			code = new SourceCode(resource("Better.src"));
			compiler.addSourceCode(code);
			compiler.compile();
			MeasurementResults results = measurer.measure(compiler.loadClasses());

			assertArrayEquals(new Integer[] { 1, 1 },
					results.getComparisonResult(clsName, "method1", m1.getParameterTypes()));
			assertArrayEquals(new Integer[] { 2, 2 },
					results.getComparisonResult(clsName, "method2", m2.getParameterTypes()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			compiler.dispose();
		}

	}

	private static InputStream resource(String name) {
		return MeasurerTest.class.getResourceAsStream("resources"
				+ separator + name.replaceAll("/", separator));
	}

}
