package gradethecode.measure;

import gradethecode.measure.exceptions.InvalidResultException;

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
		this.results.put(key, result != null ? result : new InvalidResult());
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
		private int[] comparisons;
		private long elapsedTime;

		private Result() {}

		protected Result(int[] comparisons, long speed) {
			this.comparisons = comparisons;
			this.elapsedTime = speed;
		}

		public int[] getComparisons() {
			return comparisons;
		}

		public long getElapsedTime() {
			return elapsedTime;
		}
	}

	private class InvalidResult extends Result {
		@Override
		public int[] getComparisons() {
			throw new InvalidResultException();
		}
		@Override
		public long getElapsedTime() {
			throw new InvalidResultException();
		}
	}

}
