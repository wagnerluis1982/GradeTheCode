package org.gtc.grade;

public class GradeResult {

	private String name;
	private double grade;
	private String notes;

	public GradeResult(String name, double grade, CharSequence notes) {
		this.name = name;
		this.grade = grade;
		this.notes = (String) notes;
	}

	public String getName() {
		return name;
	}

	public double getGrade() {
		return grade;
	}

	public String getNotes() {
		return notes;
	}

}
