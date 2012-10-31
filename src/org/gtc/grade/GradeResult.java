package org.gtc.grade;

/**
 * Class to carry grade data
 *
 * @author Wagner Macedo
 */
public class GradeResult {

	private String name;
	private double grade;
	private String notes;

	/**
	 * Construct a new {@code GradeResult} object
	 *
	 * @param name identify the {@code GradeResult}
	 * @param grade the grade itself
	 * @param notes extra data about the grade
	 */
	public GradeResult(String name, double grade, CharSequence notes) {
		this.name = name;
		this.grade = grade;
		this.notes = notes.toString();
	}

	/** Identify the {@code GradeResult} */
	public String getName() {
		return name;
	}

	/** The grade value itself */
	public double getGrade() {
		return grade;
	}

	/** Some notes about the grade. Extra data. */
	public String getNotes() {
		return notes;
	}

}
