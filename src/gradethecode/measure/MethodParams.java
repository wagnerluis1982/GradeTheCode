package gradethecode.measure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodParams implements Comparable<MethodParams> {

	private static final Class<?>[] NO_CLASSES = new Class<?>[0];
	private static final Object[] NO_OBJECTS = new Object[0];
	private String name;
	private Class<?>[] parameterTypes;;
	private Class<?> returnType;

	private List<Object[]> comparingRules;

	public MethodParams(String name, Class<?>[] parametersType, Class<?> returnType) {
		this.name = name;
		this.parameterTypes = parametersType != null ? parametersType : NO_CLASSES;
		this.returnType = returnType;

		this.comparingRules = new ArrayList<Object[]>();
	}

	public void addComparisonRule(Object[] parameterValues, Object returnValue) {
		// prevent parameterValues to add null to the rules
		parameterValues = parameterValues != null ? parameterValues : NO_OBJECTS;

		this.comparingRules.add(new Object[] {parameterValues, returnValue});
	}

	public List<Object[]> getComparisonRules() {
		return Collections.unmodifiableList(comparingRules);
	}

	public String getName() {
		return name;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public boolean equals(Object obj) {
		MethodParams o = (MethodParams) obj;

		return this.name.equals(o.name) &&
				Arrays.equals(this.parameterTypes, o.parameterTypes) &&
				this.returnType.equals(o.returnType);
	}

	@Override
	public int compareTo(MethodParams o) {
		if (this.equals(o))
			return 0;

		return (this.name + this.parameterTypes + this.returnType)
						.compareTo(o.name + o.parameterTypes + o.returnType);
	}

}
