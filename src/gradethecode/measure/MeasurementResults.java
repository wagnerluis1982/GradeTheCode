package gradethecode.measure;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class MeasurementResults {

	private Map<String, Integer[]> comparing;

	public MeasurementResults() {
		this.comparing = new TreeMap<String, Integer[]>();
	}

	public void setComparisonResult(String className, String methName, Class<?>[] parameterTypes,
			Integer[] result) {
		String key = createKey(className, methName, parameterTypes);
		this.comparing.put(key, result);
	}

	public Integer[] getComparisonResult(String className, String methName, Class<?>[] parameterTypes) {
		String key = createKey(className, methName, parameterTypes);
		return this.comparing.get(key);
	}

	private String createKey(String className, String methName, Class<?>[] classes) {
		return String.format("%s->%s(%s)", className, methName, Arrays.toString(classes));
	}

}
