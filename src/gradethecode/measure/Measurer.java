package gradethecode.measure;

import static java.lang.String.format;

import gradethecode.Compiler;
import gradethecode.SourceCode;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.DuplicateSourceCodeException;
import gradethecode.measure.MeasurementResults.Result;
import gradethecode.measure.exceptions.MissingClassException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Measurer {

	private Set<ClassParams> params;

	public Measurer(Set<ClassParams> params) {
		this.params = params;
	}

	public MeasurementResults measure(Map<String, Class<?>> classes)
			throws MissingClassException, NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException {
		MeasurementResults results = new MeasurementResults();

		for (ClassParams cp : this.params) {
			String clsName = cp.getName();

			if (!classes.containsKey(clsName))
				throw new MissingClassException(format("class %s is missing", clsName));
			Class<?> cls = classes.get(clsName);
			Object obj = cls.newInstance();

			for (MethodParams mp : cp.getMethods()) {
				String methName = mp.getName();
				Method method = cls.getDeclaredMethod(methName, mp.getParameterTypes());

				// if return type is invalid, all other measures in this
				// method will not be done
				if (!method.getReturnType().isAssignableFrom(mp.getReturnType())) {
					results.setResult(clsName, methName, mp.getParameterTypes(), null);
					continue;
				}

				List<Object[]> rules = mp.getComparisonRules();
				int hits = 0;
				List<Long> elapsedTimes = new ArrayList<Long>();
				for (Object[] rule : rules) {
					Object expected = rule[1];

					// invoke method as much as needed to calculate elapsed time
					Object value = null;
					long startTime = System.nanoTime();
					do {
						long start = System.nanoTime();
						value = method.invoke(obj, (Object[])rule[0]);
						elapsedTimes.add(System.nanoTime() - start);
					} while (System.nanoTime() - startTime < 50000);

					if (expected == null ? value == null : expected.equals(value))
						hits++;
				}

				long elapsedTime = 0;
				for (long time : elapsedTimes)
					elapsedTime += time;
				elapsedTime /= elapsedTimes.size();

				Result r = new Result(new int[] {hits, rules.size()}, elapsedTime);
				results.setResult(clsName, methName, mp.getParameterTypes(), r);
			}
		}

		return results;
	}

	public MeasurementResults measure(SourceCode... sourceCodes)
			throws IOException, CompilerException,
			DuplicateSourceCodeException, MissingClassException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException, ClassNotFoundException {
		Compiler compiler = null;
		try {
			compiler = new Compiler();
			compiler.addSourceCode(sourceCodes);
			compiler.compile();
			return measure(compiler.loadClasses());
		} finally {
			if (compiler != null)
				compiler.dispose();
		}
	}

}
