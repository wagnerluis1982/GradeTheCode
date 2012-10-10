package gradethecode.measure;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class MeasurementResults {

	private Map<String, Result> results;

	public MeasurementResults() {
		this.results = new TreeMap<String, Result>();
	}

	public void setResult(String className, String methName,
			Class<?>[] parameterTypes, Result result) {
		String key = createKey(className, methName, parameterTypes);
		this.results.put(key, result);
	}

	public void setResult(String clsName, String methName,
			Class<?>[] parameterTypes, int code) {
		this.setResult(clsName, methName, parameterTypes, new Result(code));
	}

	public Result getResult(String className, String methName,
			Class<?>[] parameterTypes) {
		String key = createKey(className, methName, parameterTypes);
		return results.get(key);
	}

	private String createKey(String className, String methName,
			Class<?>[] parameterTypes) {
		return String.format("%s->%s(%s)", className, methName,
				Arrays.toString(parameterTypes));
	}

	public static class Result {
		protected final static int SUCCESS = 0;
		protected final static int ERROR_RTYPE = 1;
		protected final static int ERROR_ARGS = 2;
		protected static final int ERROR_RULE = 3;

		private int[] comparisons;
		private long elapsedTime;
		private int code;

		protected Result(int[] comparisons, long speed) {
			this.comparisons = comparisons;
			this.elapsedTime = speed;
		}

		private Result(int code) {
			this.code = code;
		}

		public int[] getComparisons() {
			return comparisons;
		}

		public long getElapsedTime() {
			return elapsedTime;
		}


		public int getErrorCode() {
			return code;
		}

	}
}
