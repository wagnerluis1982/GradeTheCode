package gradethecode;

import gradethecode.exceptions.SourceCodeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCode {

	private String code;
	private String packageName;
	private String className;
	private SourceCodeObject sourceCodeObject;

	public SourceCode(String code) {
		if (code == null)
			throw new IllegalArgumentException("code should not be null");

		this.setAttributes(code);
	}

	public SourceCode(File file) {
		if (!(file.isFile() && file.canRead()))
			throw new SourceCodeException("could not read file " + file);

		try {
			this.setAttributes(new Scanner(file).useDelimiter("\\A").next());
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("empty source code");
		} catch (FileNotFoundException e) {
			// this block exists for the code stay in conformity with jvm rules
		}

		this.setPackageName(this.code);
	}

	private void setAttributes(String code) {
		this.code = code;
		this.setPackageName(code);
		this.setClassName(code);
	}

	private void setPackageName(String code) {
		// regex to match package name
		// NOTE: this pattern is assuming the code is well formatted
		Pattern pattern = Pattern.compile(
				// junk before the package name
				"(?:\\s|(?m)^\\s*//.*$|/\\*.*\\*/)*(?:\\bpackage\\b\\s*)" +
				// grouping the package name
				"([^;]*)" +
				// rest of the code
				"(?:;.*)", Pattern.DOTALL);

		Matcher matcher = pattern.matcher(code);
		if (matcher.matches())
			this.packageName = matcher.group(1).replaceAll("\\s", "");
		else
			this.packageName = "";
	}

	public String getPackageName() {
		return this.packageName;
	}

	private void setClassName(String code) {
		// regex to match class name
		// NOTE: this pattern is assuming the code is well formatted
		Pattern pattern = Pattern.compile(
				"(?:.*class\\s*)(\\b\\w*\\b)(?:.*)", Pattern.DOTALL);

		Matcher matcher = pattern.matcher(code);
		if (matcher.matches())
			this.className = matcher.group(1);
		else
			throw new SourceCodeException("code has not a defined class");
	}

	public String getClassName() {
		return this.className;
	}

	public SourceCodeObject getSourceCodeObject() {
		if (this.sourceCodeObject == null) {
			this.sourceCodeObject = new SourceCodeObject(
					this.getPackageName() + "." + this.getClassName(),
					this.code);
		}

		return this.sourceCodeObject;
	}

	@Override
	public String toString() {
		return this.code;
	}



}
