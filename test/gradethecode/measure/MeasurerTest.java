package gradethecode.measure;

import static org.junit.Assert.*;
import static org.hamcrest.number.OrderingComparison.*;
import static java.io.File.separator;
import static gradethecode.measure.Measurer.nanoSeconds;

import gradethecode.SourceCode;
import gradethecode.exceptions.ClassNotDefinedException;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.DuplicateSourceCodeException;
import gradethecode.exceptions.EmptyCodeException;
import gradethecode.measure.MeasurementResults.Result;
import gradethecode.measure.exceptions.MissingClassException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurerTest {

	protected static ClassParams clp;
	protected static MethodParams method1;
	protected static MethodParams method2;
	protected static Measurer measurer;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException {
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

	@Test
	public void testMeasure() throws DuplicateSourceCodeException,
			EmptyCodeException, ClassNotDefinedException,
			MissingClassException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException, IOException, CompilerException {
		MeasurementResults results;
		Result r;

		// Testing the best code
		results = measurer.measure(new SourceCode(resource("Better.src")));

		// method1 results
		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(2)));

		// method2 results
		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(3)));

		// Testing a poor code
		results = measurer.measure(new SourceCode(resource("Poor.src")));

		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(9)));

		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {1, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(10)));

		// Now, testing two classes

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
		results = measurer.measure(new SourceCode(resource("Better.src")),
				new SourceCode(resource("OtherClass.src")));

		// Better.src
		r = results.getResult(clp.getName(), method1.getName(), method1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(2)));

		r = results.getResult(clp.getName(), method2.getName(), method2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
		assertThat(r.getElapsedTime(), greaterThanOrEqualTo(nanoSeconds(3)));

		// OtherClass.src - elapsed time isn't tested here because is not deterministic
		r = results.getResult(oClp.getName(), value1.getName(), value1.getParameterTypes());
		assertArrayEquals(new int[] {1, 1}, r.getComparisons());

		r = results.getResult(oClp.getName(), value2.getName(), value2.getParameterTypes());
		assertArrayEquals(new int[] {2, 2}, r.getComparisons());
	}

	protected static InputStream resource(String name) {
		return MeasurerTest.class.getResourceAsStream("resources"
				+ separator + name.replaceAll("/", separator));
	}

	protected static <T> Set<T> asSet(T ...items) {
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(items));
		return set;
	}

}
