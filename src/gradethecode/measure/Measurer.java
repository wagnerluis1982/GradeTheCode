package gradethecode.measure;

import static java.lang.String.format;

import gradethecode.Compiler;
import gradethecode.SourceCode;
import gradethecode.exceptions.CompilerException;
import gradethecode.exceptions.DuplicateSourceCodeException;
import gradethecode.measure.MeasurementResults.Result;
import gradethecode.measure.exceptions.MissingClassException;

import java.io.IOException;
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

	public MeasurementResults measure(Map<String, Class<?>> classes) throws MissingClassException, InstantiationException, IllegalAccessException {
		MeasurementResults results = new MeasurementResults();

		for (ClassParams cp : this.params) {
			String clsName = cp.getName();

			if (!classes.containsKey(clsName))
				throw new MissingClassException(format("class %s is missing", clsName));
			Class<?> cls = classes.get(clsName);
			Object obj = cls.newInstance();

			for (MethodParams mp : cp.getMethods()) {
				String methName = mp.getName();
				Method method = null;

				try {
					method = cls.getDeclaredMethod(methName, mp.getParameterTypes());
				} catch (Exception e) {
					results.setResult(clsName, methName, mp.getParameterTypes(),
							Result.ERROR_ARGS);
					continue;
				}

				// if return type is invalid, all other measures in this
				// method will not be done
				if (!method.getReturnType().isAssignableFrom(mp.getReturnType())) {
					results.setResult(clsName, methName, mp.getParameterTypes(),
							Result.ERROR_RTYPE);
					continue;
				}

				List<Object[]> rules = mp.getComparisonRules();
				List<Long> elapsedTimes = new ArrayList<Long>();
				int hits = 0;
				for (Object[] rule : rules) {
					Object expected = rule[1];

					try {
					// invoke method one time to see if value is OK
					long hardStartTime = System.nanoTime();
					Object value = method.invoke(obj, (Object[])rule[0]);
					if (expected == null ? value == null : expected.equals(value)) {
						elapsedTimes.add(System.nanoTime() - hardStartTime);
						hits++;

						// invoke method as much as needed to calculate elapsed time
						while (System.nanoTime() - hardStartTime < 50000) {
							long start = System.nanoTime();
							value = method.invoke(obj, (Object[])rule[0]);
							elapsedTimes.add(System.nanoTime() - start);
						}
					}
					} catch (Exception e) {
						results.setResult(clsName, methName, mp.getParameterTypes(),
								Result.ERROR_RULE);
						continue;
					}
				}

				long elapsed = 0;
				for (long time : elapsedTimes)
					elapsed += time;
				elapsed /= elapsedTimes.size();

				Result r = new Result(new int[] {hits, rules.size()}, elapsed);
				results.setResult(clsName, methName, mp.getParameterTypes(), r);
			}
		}

		return results;
	}

	public MeasurementResults measure(SourceCode... sourceCodes)
			throws IOException, CompilerException,
			DuplicateSourceCodeException, MissingClassException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
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

	protected static long nanoSeconds(long millis) {
		return millis * 1000000;
	}
}
