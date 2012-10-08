package gradethecode.measure;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class MeasurementParams {

	private Set<ClassParams> setOfParams;

	public MeasurementParams() {
		this.setOfParams = new TreeSet<ClassParams>();
	}

	public void addClassParams(ClassParams params) {
		setOfParams.add(params);
	}

	public Set<ClassParams> getSetOfParams() {
		return Collections.unmodifiableSet(setOfParams);
	}

}
