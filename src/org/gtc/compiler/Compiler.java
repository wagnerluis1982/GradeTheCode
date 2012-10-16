package org.gtc.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;

public class Compiler {
	private File targetDir;
	private Set<SourceCode> codes;
	private boolean assertionsEnabled = false;
	private Map<String, ClassWrapper> classesMap;

	public Compiler(File targetDir, SourceCode ...codes) throws InvalidTargetException {
		if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new InvalidTargetException("invalid target directory");

		prepare(targetDir, codes);
	}

	public Compiler(SourceCode ...codes) throws IOException {
		File tempDir = null;
		try {
			tempDir = Util.createTempDir();
		} catch (IllegalStateException e) {
			throw new IOException(e.getMessage());
		} finally {
			tempDir.deleteOnExit();
		}

		prepare(tempDir, codes);
	}

	private void prepare(File dir, SourceCode ...codes) {
		this.targetDir = dir;
		this.codes = new HashSet<SourceCode>(Arrays.asList(codes));

		if (this.codes.size() != codes.length)
			throw new DuplicatedCodeException("duplicated code detected");
		else if (this.codes.contains(null))
			throw new IllegalArgumentException("null found in the set");
	}

	public void enableAssertions() {
		this.assertionsEnabled  = true;
	}

	public void compile(PrintStream out) throws CompilerException {
		List<JavaFileObject> fileObjects = new ArrayList<JavaFileObject>();
		for (SourceCode sourceCode : this.codes)
			fileObjects.add(sourceCode.getJavaFileObject());

		// Compilation options
		List<String> options = Arrays.asList("-d", this.targetDir.getAbsolutePath());

		// Use diagnostics passed by argument or a local if arg is null
		DiagnosticCollector<JavaFileObject> diagnostics =
				new DiagnosticCollector<JavaFileObject>();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = compiler.getStandardFileManager(null,
				Locale.getDefault(), null);
		CompilationTask compilerTask = compiler.getTask(null, fileManager,
				diagnostics, options, null, fileObjects);

		// Call compiler
		compilerTask.call();

		// Use an OutputStream passed by argument or stdout
		out = out != null ? out : System.out;
		for (Diagnostic<?> d : diagnostics.getDiagnostics())
			out.printf("Error on line %d in %s\n", d.getLineNumber(), d);

		try {
			loadClasses();
		} catch (ClassNotFoundException e) {
			throw new CompilerException(e.getMessage() + " not found");
		} finally {
			this.deleteDir(this.targetDir, false);
		}
	}

	public void compile() throws CompilerException {
		this.compile(null);
	}

	public ClassWrapper[] getAssertionClasses() {
		return null;
	}

	public Map<String, ClassWrapper> getClasses() {
		return this.classesMap;
	}

	private void loadClasses() throws ClassNotFoundException {
		Map<String, ClassWrapper> classesMap = new HashMap<String, ClassWrapper>();

		ClassLoader classLoader = null;
		try {
			classLoader = new URLClassLoader(new URL[] {this.targetDir.toURI().toURL()});
		} catch (MalformedURLException e) {
			this.classesMap = null;
			return;
		}

		for (SourceCode code : this.codes) {
			String name = code.getName();
			classLoader.setDefaultAssertionStatus(assertionsEnabled);
			Class<?> klass = classLoader.loadClass(name);
			classesMap.put(name, new ClassWrapper(klass));
		}

		this.classesMap = Collections.unmodifiableMap(classesMap);
	}

	private void deleteDir(File dir, boolean including) {
		if (dir.exists()) {
			File[] files = dir.listFiles();

			for (File f : files) {
				if (f.isDirectory())
					this.deleteDir(f, true);
				else
					f.delete();
			}
		}

		if (including)
			dir.delete();
	}

	public File getTargetDir() {
		return targetDir;
	}




}
