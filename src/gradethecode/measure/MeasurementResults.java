package gradethecode.measure;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class MeasurementResults {

	private Map<String, int[]> comparison;

	public MeasurementResults() {
		this.comparison = new TreeMap<String, int[]>();
	}

	public void setComparisonResult(String className, String methName,
			Class<?>[] parameterTypes, int[] result) {
		String key = createKey(className, methName, parameterTypes);
		this.comparison.put(key, result);
	}

	public int[] getComparisonResult(String className, String methName,
			Class<?>[] parameterTypes) {
		String key = createKey(className, methName, parameterTypes);
		return this.comparison.get(key);
	}

	private String createKey(String className, String methName,
			Class<?>[] parameterTypes) {
		return String.format("%s->%s(%s)", className, methName,
				Arrays.toString(parameterTypes));
	}

}
