package org.gtc.sourcecode;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class SourceCode implements Comparable<SourceCode> {

	private CompilationUnit code;
	private String packageName;
	private String className;
	private JavaFileObject javaFileObject;
	private String name;

	public SourceCode(String code) throws ParseException {
		if (code == null)
			throw new IllegalArgumentException("code could not be null");

		this.identifyElements(new ByteArrayInputStream(code.getBytes()));
	}

	public SourceCode(InputStream stream) throws ParseException {
		if (stream == null)
			throw new IllegalArgumentException("argument could not be null");

		this.identifyElements(stream);
	}

	public SourceCode(File file) throws ParseException, FileNotFoundException {
		this.identifyElements(new FileInputStream(file));
	}

	private void identifyElements(InputStream stream) throws ParseException {
		CompilationUnit cUnit = JavaParser.parse(stream);
		PackageDeclaration pkgDecl = cUnit.getPackage();

		this.packageName = pkgDecl != null ? pkgDecl.getName().toString() : null;
		this.className = cUnit.getTypes().get(0).getName();
		this.name = this.packageName != null ?
				this.packageName + "." + this.className : this.className;
		this.code = cUnit;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public String getClassName() {
		return this.className;
	}

	public String getName() {
		return this.name;
	}

	public String getCode() {
		return this.code.toString();
	}

	public JavaFileObject getJavaFileObject() {
		if (this.javaFileObject == null)
			this.javaFileObject = new StringJavaFileObject(this.name,
					this.code.toString());

		return this.javaFileObject;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;

		return this.name.equals(((SourceCode) obj).name);
	}

	@Override
	public int compareTo(SourceCode o) {
		if (this.equals(o))
			return 0;

		return this.name.compareTo(o.name);
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

}
