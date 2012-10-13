package org.gtc.compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
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

public class Compiler {
	private File targetDir;
	private Set<SourceCode> codes;

	public Compiler(File targetDir) throws CompilerException {
		if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new CompilerException("invalid target directory");

		this.targetDir = targetDir;
		this.codes = new HashSet<SourceCode>();
	}

	public Compiler() throws IOException {
		File tempDir = Files.createTempDirectory("gtc_").toFile();
		tempDir.deleteOnExit();

		this.targetDir = tempDir;
		this.codes = new HashSet<SourceCode>();
	}

	public Map<String, ClassWrapper> compile() throws CompilerException {
		List<JavaFileObject> classes = new ArrayList<JavaFileObject>();
		for (SourceCode sourceCode : this.codes)
			classes.add(sourceCode.getJavaFileObject());

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = compiler.getStandardFileManager(null,
				Locale.getDefault(), null);
		List<String> options = Arrays.asList("-d", this.targetDir.getAbsolutePath());
		DiagnosticCollector<JavaFileObject> diagnostics =
				new DiagnosticCollector<JavaFileObject>();
		CompilationTask compilerTask = compiler.getTask(null, fileManager,
				diagnostics, options, null, classes);

		if (!compilerTask.call()) {
			for (Diagnostic<?> d : diagnostics.getDiagnostics())
				System.out.printf("Error on line %d in %s", d.getLineNumber(), d);
		}

		try {
			return loadClasses();
		} catch (ClassNotFoundException e) {
			throw new CompilerException(e.getMessage());
		} finally {
			this.deleteDir(this.targetDir, false);
		}
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

	public void reset() {
		this.codes.clear();
	}

	public void addSourceCode(SourceCode ...sourceCodes) throws DuplicateCodeException {
		for (SourceCode code : sourceCodes)
			if (this.codes.contains(code))
				throw new DuplicateCodeException(code.getQualifiedName()
						+ " already exists in this set");

		this.codes.addAll(Arrays.asList(sourceCodes));
	}

	public File getTargetDir() {
		return targetDir;
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

}
