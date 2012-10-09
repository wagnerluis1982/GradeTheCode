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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeasurerTest {

	protected static ClassParams cls;
	protected static MethodParams method1;
	protected static MethodParams method2;
	protected static Measurer measurer;
	protected static Compiler compiler;

	@BeforeClass
	public static void setUpClass() throws IOException, CompilerException {
		compiler = new Compiler();

		// Creating a class params, adding methods to it and defining them
		cls = new ClassParams("com.test.Test");
		// method1
		method1 = cls.addMethod("method1", null, int.class);
		method1.addComparisonRule(null, 45);
		// method2
		method2 = cls.addMethod("method2", new Class[] {int.class}, int.class);
		method2.addComparisonRule(new Object[] {10}, 1);
		method2.addComparisonRule(new Object[] {-1}, 0);

		// Create a Measurer with a set of class params
		measurer = new Measurer(asSet(cls));
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

		// Testing a better code
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Better.src")));
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());

		assertArrayEquals(new int[] {1, 1},
				results.getComparisonResult(cls.getName(), method1.getName(),
						method1.getParameterTypes()));
		assertArrayEquals(new int[] {2, 2},
				results.getComparisonResult(cls.getName(), method2.getName(),
						method2.getParameterTypes()));

		// Testing a poor code
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Poor.src")));
		compiler.compile();
		results = measurer.measure(compiler.loadClasses());

		assertArrayEquals(new int[] {1, 1},
				results.getComparisonResult(cls.getName(), method1.getName(),
						method1.getParameterTypes()));
		assertArrayEquals(new int[] {1, 2},
				results.getComparisonResult(cls.getName(), method2.getName(),
						method2.getParameterTypes()));

		// Testing two classes
		compiler.reset();
		compiler.addSourceCode(new SourceCode(resource("Better.src")));
		compiler.addSourceCode(new SourceCode(resource("OtherClass.src")));
		compiler.compile();

		// Other class params
		ClassParams oCls = new ClassParams("org.othertest.OtherClass");
		// value1
		MethodParams value1 = oCls.addMethod("value1", null, int.class);
		value1.addComparisonRule(null, 100);
		// value2
		MethodParams value2 = oCls.addMethod("value2", new Class[] {int.class}, int.class);
		value2.addComparisonRule(new Object[] {3}, 9);
		value2.addComparisonRule(new Object[] {2}, 4);

		Measurer measurer = new Measurer(asSet(cls, oCls));;
		results = measurer.measure(compiler.loadClasses());

		assertArrayEquals(new int[] {1, 1},
				results.getComparisonResult(cls.getName(), method1.getName(),
						method1.getParameterTypes()));
		assertArrayEquals(new int[] {2, 2},
				results.getComparisonResult(cls.getName(), method2.getName(),
						method2.getParameterTypes()));

		assertArrayEquals(new int[] {1, 1},
				results.getComparisonResult(oCls.getName(), value1.getName(),
						value1.getParameterTypes()));
		assertArrayEquals(new int[] {2, 2},
				results.getComparisonResult(oCls.getName(), value2.getName(),
						value2.getParameterTypes()));
	}

	protected InputStream resource(String name) {
		return MeasurerTest.class.getResourceAsStream("resources"
				+ separator + name.replaceAll("/", separator));
	}

	protected static <T> Set<T> asSet(T ...items) {
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(items));
		return set;
	}

}
