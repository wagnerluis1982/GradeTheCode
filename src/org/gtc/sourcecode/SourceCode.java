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

import org.gtc.compiler.Compiler;

/**
 * Class that represents a java class source code
 *
 * @author Wagner Macedo
 */
public class SourceCode implements Comparable<SourceCode> {

	private CompilationUnit code;
	private String packageName;
	private String className;
	private JavaFileObject javaFileObject;
	private String name;

	/**
	 * Construct a {@code SourceCode} object using a {@link String} as code.
	 *
	 * @param code the java code
	 * @throws ParseException
	 */
	public SourceCode(String code) throws ParseException {
		if (code == null)
			throw new IllegalArgumentException("code could not be null");

		this.identifyElements(new ByteArrayInputStream(code.getBytes()));
	}

	/**
	 * Construct a {@code SourceCode} object using a {@link InputStream} as code.
	 * @param stream the {@link InputStream} that carry the code
	 * @throws ParseException
	 */
	public SourceCode(InputStream stream) throws ParseException {
		if (stream == null)
			throw new IllegalArgumentException("argument could not be null");

		this.identifyElements(stream);
	}

	/**
	 * Construct a {@code SourceCode} object reading the code from a {@link File}.
	 * @param file the {@link File} that carry the code
	 * @throws ParseException
	 * @throws FileNotFoundException
	 */
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

	/**
	 * Get the package name identified in the code.
	 *
	 * @return the java package name
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * Get the top class name identified in the code.
	 *
	 * @return the java top class name
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Get the qualified name identified in the code
	 *
	 * <p> The qualified name is the join between the package and class name.
	 * {@code String} is a class name, but {@code java.lang.String} is a
	 * qualified.
	 *
	 * @return the java qualified name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the code used to create the {@code SourceCode} object.
	 *
	 * @return the source code
	 */
	public String getCode() {
		return this.code.toString();
	}

	/**
	 * Get the {@link JavaFileObject} created using the code passed.
	 *
	 * <p> {@link JavaFileObject} is used by the {@link Compiler} class.
	 *
	 * @return a {@link JavaFileObject} representing the code
	 */
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
