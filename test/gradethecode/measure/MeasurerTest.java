package gradethecode.measure;

import static org.junit.Assert.*;
import static java.io.File.separator;

import gradethecode.SourceCode;
import gradethecode.Compiler;
import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.EmptyCodeException;
import gradethecode.measure.exceptions.MissingClassException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurerTest {

	protected static MeasurementParams params;
	protected static ClassParams cls;
	protected static MethodParams method1;
	protected static MethodParams method2;
	protected static Measurer measurer;
	protected static Compiler compiler;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException {
		compiler = new Compiler();

		// Defining method1
		method1 = new MethodParams("method1", null, int.class);
		method1.addComparisonRule(null, 45);

		// Defining method2
		method2 = new MethodParams("method2", new Class[] {int.class}, int.class);
		method2.addComparisonRule(new Object[] {10}, 1);
		method2.addComparisonRule(new Object[] {-1}, 0);

		// Adding methods to class
		cls = new ClassParams("com.test.Test");
		cls.addMethodParams(method1);
		cls.addMethodParams(method2);

		// Adding ClassParams to the MeasurementParams
		params = new MeasurementParams();
		params.addClassParams(cls);

		// Create a Measurer with the MeasurementParams
		measurer = new Measurer(params);
	}

	@AfterClass
	public static void tearDownClass() {
		compiler.dispose();
	}

	@Test
	public void testMeasure() throws EmptyCodeException,
			ClassNotDefinedException, MissingClassException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException, MalformedURLException,
			ClassNotFoundException {
		SourceCode code;
		MeasurementResults results;

		// Testing a better code
		code = new SourceCode(resource("Better.src"));
		compiler.reset();
		compiler.addSourceCode(code);
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());
		assertArrayEquals(new Integer[] {1, 1},
				results.getComparisonResult(cls.getName(), method1.getName(),
						method1.getParameterTypes()));
		assertArrayEquals(new Integer[] {2, 2},
				results.getComparisonResult(cls.getName(), method2.getName(),
						method2.getParameterTypes()));

		// Testing a poor code
		code = new SourceCode(resource("Poor.src"));
		compiler.reset();
		compiler.addSourceCode(code);
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());
		assertArrayEquals(new Integer[] {1, 1},
				results.getComparisonResult(cls.getName(), method1.getName(),
						method1.getParameterTypes()));
		assertArrayEquals(new Integer[] {1, 2},
				results.getComparisonResult(cls.getName(), method2.getName(),
						method2.getParameterTypes()));
	}

	protected InputStream resource(String name) {
		return MeasurerTest.class.getResourceAsStream("resources"
				+ separator + name.replaceAll("/", separator));
	}

}
