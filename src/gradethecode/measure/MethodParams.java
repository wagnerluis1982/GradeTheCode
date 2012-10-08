package gradethecode.measure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodParams {

	private String name;
	private Class<?>[] parameterTypes;;
	private Class<?> returnType;

	private List<Object[]> comparingRules;

	public MethodParams(String name, Class<?>[] parametersType, Class<?> returnType) {
		this.name = name;
		this.parameterTypes = parametersType != null ? parametersType : new Class<?>[0];
		this.returnType = returnType;

		this.comparingRules = new ArrayList<Object[]>();
	}

	public void addComparingRule(Object[] parameterValues, Object returnValue) {
		// prevent parameterValues to add null to the rules
		parameterValues = parameterValues != null ? parameterValues : new Object[0];

		this.comparingRules.add(new Object[] {parameterValues, returnValue});
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

	public List<Object[]> getComparingRules() {
		return Collections.unmodifiableList(comparingRules);
	}

	@Override
	public boolean equals(Object obj) {
		MethodParams o = (MethodParams) obj;

		return this.name.equals(o.name) &&
				Arrays.equals(this.parameterTypes, o.parameterTypes) &&
				this.returnType.equals(o.returnType);
	}


}
