package gradethecode.measure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodParams implements Comparable<MethodParams> {

	protected static final Class<?>[] NO_CLASSES = new Class<?>[0];
	protected static final Object[] NO_OBJECTS = new Object[0];
	protected String className;
	protected String name;
	protected Class<?>[] parameterTypes;;
	protected Class<?> returnType;
	protected List<Object[]> comparisonRules;

	protected MethodParams(String className, String name,
			Class<?>[] parametersType, Class<?> returnType) {
		this.className = className;
		this.name = name;
		this.parameterTypes = parametersType != null ? parametersType : NO_CLASSES;
		this.returnType = returnType;

		this.comparisonRules = new ArrayList<Object[]>();
	}

	public void addComparisonRule(Object[] parameterValues, Object returnValue) {
		// prevent parameterValues to add null to the rules
		parameterValues = parameterValues != null ? parameterValues : NO_OBJECTS;

		this.comparisonRules.add(new Object[] {parameterValues, returnValue});
	}

	public List<Object[]> getComparisonRules() {
		return Collections.unmodifiableList(comparisonRules);
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

	public String belongsTo() {
		return this.className;
	}

	@Override
	public boolean equals(Object obj) {
		MethodParams o = (MethodParams) obj;

		return this.className.equals(o.name) &&
				this.name.equals(o.name) &&
				Arrays.equals(this.parameterTypes, o.parameterTypes) &&
				this.returnType.equals(o.returnType);
	}

	@Override
	public int compareTo(MethodParams o) {
		if (this.equals(o))
			return 0;

		return (this.className + this.name + this.parameterTypes + this.returnType)
						.compareTo(o.className + o.name + o.parameterTypes + o.returnType);
	}

}
