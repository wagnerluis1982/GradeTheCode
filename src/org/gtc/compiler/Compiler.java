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

	public Compiler(File targetDir, SourceCode ...codes) throws InvalidTargetException, DuplicatedCodeException {
		if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new InvalidTargetException("invalid target directory");

		initialize(targetDir, codes);
	}

	public Compiler(SourceCode ...codes) throws IOException, DuplicatedCodeException {
		File tempDir = null;
		try {
			tempDir = Util.createTempDir();
		} catch (IllegalStateException e) {
			throw new IOException(e.getMessage());
		} finally {
			tempDir.deleteOnExit();
		}

		initialize(tempDir, codes);
	}

	private void initialize(File dir, SourceCode ...codes) throws DuplicatedCodeException {
		this.targetDir = dir;
		this.codes = new HashSet<SourceCode>(Arrays.asList(codes));

		if (this.codes.size() != codes.length)
			throw new DuplicatedCodeException("duplicated code detected");
		else if (this.codes.contains(null))
			throw new IllegalArgumentException("null found in the set");
	}

	public Map<String, ClassWrapper> compile(PrintStream out,
			DiagnosticCollector<JavaFileObject> diagnostics)
			throws CompilerException {
		List<JavaFileObject> fileObjects = new ArrayList<JavaFileObject>();
		for (SourceCode sourceCode : this.codes)
			fileObjects.add(sourceCode.getJavaFileObject());

		// Compilation options
		List<String> options = Arrays.asList("-d", this.targetDir.getAbsolutePath());

		// Use diagnostics passed by argument or a local if arg is null
		diagnostics = diagnostics != null ? diagnostics
				: new DiagnosticCollector<JavaFileObject>();

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
			out.printf("Error on line %d in %s", d.getLineNumber(), d);

		try {
			return loadClasses();
		} catch (ClassNotFoundException e) {
			throw new CompilerException(e.getMessage());
		} finally {
			this.deleteDir(this.targetDir, false);
		}
	}

	public Map<String, ClassWrapper> compile() throws CompilerException {
		return this.compile(null, null);
	}

	private Map<String, ClassWrapper> loadClasses() throws ClassNotFoundException {
		Map<String, ClassWrapper> classesMap = new HashMap<String, ClassWrapper>();

		ClassLoader classLoader = null;
		try {
			classLoader = new URLClassLoader(new URL[] {this.targetDir.toURI().toURL()});
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		for (SourceCode code : this.codes) {
			String name = code.getQualifiedName();
			Class<?> klass = classLoader.loadClass(name);
			classesMap.put(name, new ClassWrapper(klass));
		}

		return Collections.unmodifiableMap(classesMap);
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
