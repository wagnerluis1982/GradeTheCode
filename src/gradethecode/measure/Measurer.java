package gradethecode.measure;

import static java.lang.String.format;

import gradethecode.measure.exceptions.MissingClassException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Measurer {

	private MeasurementParams params;

	public Measurer(MeasurementParams params) {
		this.params = params;
	}

	public MeasurementResults measure(Map<String, Class<?>> classes)
			throws MissingClassException, NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException {
		MeasurementResults results = new MeasurementResults();

		for (ClassParams cp : this.params.getSetOfClassParams()) {
			String clsName = cp.getName();

			if (!classes.containsKey(clsName))
				throw new MissingClassException(format("class %s is missing", clsName));
			Class<?> cls = classes.get(clsName);
			Object obj = cls.newInstance();

			for (MethodParams mp : cp.getSetOfMethodParams()) {
				String methName = mp.getName();
				Method method = cls.getDeclaredMethod(methName, mp.getParameterTypes());

				// if return type is invalid, all other measures in this
				// method will not be done
				if (!method.getReturnType().isAssignableFrom(mp.getReturnType())) {
					results.setComparisonResult(clsName, methName, mp.getParameterTypes(), null);
					continue;
				}

				List<Object[]> rules = mp.getComparisonRules();
				int hits = 0;
				for (Object[] rule : rules) {
					Object expected = rule[1];
					Object value = method.invoke(obj, (Object[])rule[0]);

					if (expected == null ? value == null : expected.equals(value))
						hits++;
				}
				results.setComparisonResult(clsName, methName, mp.getParameterTypes(),
						new Integer[] {hits, rules.size()});
			}
		}

		return results;
	}

}
