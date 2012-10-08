package gradethecode.measure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassParams implements Comparable<ClassParams> {

	private String name;
	private Set<MethodParams> setOfMethods;

	public ClassParams(String name) {
		this.name = name;
		this.setOfMethods = new HashSet<MethodParams>();
	}

	public void addMethodParam(MethodParams method) {
		this.setOfMethods.add(method);
	}

	public String getName() {
		return name;
	}

	public Set<MethodParams> getSetOfMethodParams() {
		return Collections.unmodifiableSet(setOfMethods);
	}

	@Override
	public boolean equals(Object obj) {
		ClassParams o = (ClassParams) obj;

		return this.name.equals(o.name) &&
				this.setOfMethods.equals(o.setOfMethods);
	}


	@Override
	public int compareTo(ClassParams o) {
		if (this.equals(o))
			return 0;

		return (this.name + this.setOfMethods).compareTo(o.name + o.setOfMethods);
	}

}
