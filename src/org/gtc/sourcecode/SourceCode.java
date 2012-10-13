package org.gtc.sourcecode;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class SourceCode implements Comparable<SourceCode> {

	private String code;
	private String packageName;
	private String className;
	private JavaFileObject javaFileObject;
	private String qualifiedName;

	public SourceCode(String code) throws ClassNotDefinedException {
		if (code == null)
			throw new IllegalArgumentException("code should not be null");

		this.identifyElements(code);
	}

	public SourceCode(InputStream stream) throws EmptyCodeException,
			ClassNotDefinedException {
		try {
			this.identifyElements(new Scanner(stream).useDelimiter("\\A").next());
		} catch (NoSuchElementException e) {
			throw new EmptyCodeException("empty source code");
		}
	}

	public SourceCode(File file) throws FileNotReadException,
			EmptyCodeException, ClassNotDefinedException {
		if (!(file.isFile() && file.canRead()))
			throw new FileNotReadException("could not read file " + file);

		try {
			this.identifyElements(new Scanner(file).useDelimiter("\\A").next());
		} catch (NoSuchElementException e) {
			throw new EmptyCodeException("empty source code");
		} catch (FileNotFoundException e) {
			// this block exists for the code stay in conformity with jvm rules
		}
	}

	private void identifyElements(String code) throws ClassNotDefinedException {
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

		// regex to match class name
		// NOTE: this pattern is assuming the code is well formatted
		pattern = Pattern.compile(
				"(?:.*class\\s*)(\\b\\w*\\b)(?:.*)", Pattern.DOTALL);

		matcher = pattern.matcher(code);
		if (matcher.matches())
			this.className = matcher.group(1);
		else
			throw new ClassNotDefinedException("code has not a defined class");

		if (this.packageName != null)
			this.qualifiedName = this.packageName + "." + this.className;
		else
			this.qualifiedName = this.className;

		this.code = code;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public String getClassName() {
		return this.className;
	}

	public String getQualifiedName() {
		return this.qualifiedName;
	}

	public JavaFileObject getJavaFileObject() {
		if (this.javaFileObject == null) {
			this.javaFileObject = new StringJavaFileObject(this.qualifiedName,
														   this.code);
		}

		return this.javaFileObject;
	}

	@Override
	public String toString() {
		return this.code;
	}

	private class StringJavaFileObject extends SimpleJavaFileObject {
		private CharSequence charContent;

		protected StringJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name.replaceAll("\\.", "/")
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.charContent = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
				throws IOException {
			return charContent;
		}
	}


	@Override
	public boolean equals(Object obj) {
		SourceCode o = (SourceCode) obj;
		return this.qualifiedName == o.qualifiedName;
	}

	@Override
	public int compareTo(SourceCode o) {
		return this.qualifiedName.compareTo(o.qualifiedName);
	}

}
