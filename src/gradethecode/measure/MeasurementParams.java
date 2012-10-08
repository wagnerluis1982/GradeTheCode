package gradethecode.measure;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class MeasurementParams {

	private Set<ClassParams> setOfClassParams;

	public MeasurementParams() {
		this.setOfClassParams = new TreeSet<ClassParams>();
	}

	public void addClassParams(ClassParams params) {
		setOfClassParams.add(params);
	}

	public Set<ClassParams> getSetOfClassParams() {
		return Collections.unmodifiableSet(setOfClassParams);
	}

}
