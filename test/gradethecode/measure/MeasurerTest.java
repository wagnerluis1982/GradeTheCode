package gradethecode.measure;

import static org.junit.Assert.*;
import static java.io.File.separator;
import static org.hamcrest.number.OrderingComparison.*;

import gradethecode.SourceCode;
import gradethecode.Compiler;
import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.EmptyCodeException;
import gradethecode.measure.MeasurementResults.Result;
import gradethecode.measure.exceptions.MissingClassException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurerTest {

	protected static ClassParams clp;
	protected static MethodParams method1;
	protected static MethodParams method2;
	protected static Measurer measurer;
	protected static Compiler compiler;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException {
		compiler = new Compiler();

		// Creating a class params, adding methods to it and defining them
		clp = new ClassParams("com.test.Test");
		// method1
		method1 = clp.addMethod("method1", null, int.class);
		method1.addComparisonRule(null, 45);
		// method2
		method2 = clp.addMethod("method2", new Class[] {int.class}, int.class);
		method2.addComparisonRule(new Object[] {10}, 1);
		method2.addComparisonRule(new Object[] {-1}, 0);

		// Create a Measurer with a set of class params
		measurer = new Measurer(asSet(clp));
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
		MeasurementResults results;
		Result r;

		// Testing the best code
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Better.src")));
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());

		// method1 results
		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(2)));

		// method2 results
		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(3)));

		// Testing a poor code
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Poor.src")));
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());

		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(9)));

		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {1, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(10)));

		// Testing two classes
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Better.src")));
		compiler.addSourceCode(new SourceCode(resource("OtherClass.src")));
		compiler.compile();

		// Other class params
		ClassParams oClp = new ClassParams("org.othertest.OtherClass");
		// value1
		MethodParams value1 = oClp.addMethod("value1", null, int.class);
		value1.addComparisonRule(null, 100);
		// value2
		MethodParams value2 = oClp.addMethod("value2", new Class[] {int.class}, int.class);
		value2.addComparisonRule(new Object[] {3}, 9);
		value2.addComparisonRule(new Object[] {2}, 4);

		Measurer measurer = new Measurer(asSet(clp, oClp));;
		results = measurer.measure(compiler.loadClasses());

		// Better.src
		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(2)));

		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(3)));

		// OtherClass.src - this code doesn't test elapsed time because isn't deterministic
		r = results.getResult(oClp.getName(), value1.getName(), value1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());

		r = results.getResult(oClp.getName(), value2.getName(), value2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
	}

	protected InputStream resource(String name) {
		return MeasurerTest.class.getResourceAsStream("resources"
				+ separator + name.replaceAll("/", separator));
	}

	protected long nanoSeconds(long millis) {
		return millis * 1000000;
	}

	protected static <T> Set<T> asSet(T ...items) {
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(items));
		return set;
	}

}
