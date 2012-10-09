package gradethecode.measure;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ClassParams implements Comparable<ClassParams> {

	private String name;
	private Set<MethodParams> setOfMethodParams;

	public ClassParams(String name) {
		this.name = name;
		this.setOfMethodParams = new TreeSet<MethodParams>();
	}

	public String getName() {
		return name;
	}

	public MethodParams addMethod(String name, Class<?>[] parametersType, Class<?> returnType) {
		MethodParams params = new MethodParams(name, parametersType, returnType);
		this.setOfMethodParams.add(params);

		return params;
	}

	public Set<MethodParams> getMethods() {
		return Collections.unmodifiableSet(setOfMethodParams);
	}

	@Override
	public boolean equals(Object obj) {
		ClassParams o = (ClassParams) obj;

		return this.name.equals(o.name) &&
				this.setOfMethodParams.equals(o.setOfMethodParams);
	}

	@Override
	public int compareTo(ClassParams o) {
		if (this.equals(o))
			return 0;

		return (this.name + this.setOfMethodParams)
					.compareTo(o.name + o.setOfMethodParams);
	}

}
