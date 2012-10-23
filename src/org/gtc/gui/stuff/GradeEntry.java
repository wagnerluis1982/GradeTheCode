package org.gtc.gui.stuff;

public class GradeEntry {

	private String name;
	private double grade;
	private String notes;

	public GradeEntry(String name, double grade, String notes) {
		this.name = name;
		this.grade = grade;
		this.notes = notes;
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
