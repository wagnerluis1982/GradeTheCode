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
	private List<SourceCode> assertionCodes;
	private Map<String, ClassWrapper> classesMap;

	public Compiler(File targetDir) throws InvalidTargetException {
		if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new InvalidTargetException("invalid target directory");

		initialize(targetDir);
	}

	public Compiler() throws IOException {
		File tempDir = null;
		try {
			tempDir = Util.createTempDir();
		} catch (IllegalStateException e) {
			throw new IOException(e);
		} finally {
			tempDir.deleteOnExit();
		}

		initialize(tempDir);
	}

	private void initialize(File dir) {
		this.targetDir = dir;
		this.codes = new HashSet<SourceCode>();
		this.assertionCodes = new ArrayList<SourceCode>();
	}

	public void addCodes(SourceCode... codes) {
		codes = Util.filterNonNull(codes);

		for (SourceCode code : codes) {
			if (this.codes.contains(code))
				throw new DuplicatedCodeException("the class " + code.getName() +
						" is already in the set");
			this.codes.add(code);
		}
	}

	public void addAssertionCodes(SourceCode... codes) {
		this.addCodes(codes);
		this.assertionCodes.addAll(Arrays.asList(codes));
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

		// Clean the directory on JVM shutdown
		this.deltreeOnExit(this.targetDir);

		// Load classes in memory
		loadClasses();
	}

	public void compile() throws CompilerException {
		this.compile(null);
	}

	public ClassWrapper[] getAssertionClasses() {
		List<ClassWrapper> classes = new ArrayList<ClassWrapper>();
		for (SourceCode code : this.assertionCodes)
			classes.add(this.classesMap.get(code.getName()));

		return classes.toArray(new ClassWrapper[0]);
	}

	public Map<String, ClassWrapper> getClasses() {
		return this.classesMap;
	}

	private void loadClasses() throws CompilerException {
		Map<String, ClassWrapper> classesMap = new HashMap<String, ClassWrapper>();
		this.classesMap = null;

		ClassLoader classLoader = null;
		try {
			classLoader = new URLClassLoader(new URL[] {this.targetDir.toURI().toURL()});
		} catch (MalformedURLException e) {
			// never caught
		}

		// Activate assertions where marked
		for (SourceCode code : this.assertionCodes)
			classLoader.setClassAssertionStatus(code.getName(), true);

		// Load classes
		try {
			for (SourceCode code : this.codes) {
				String name = code.getName();
				Class<?> klass = classLoader.loadClass(name);
				classesMap.put(name, new ClassWrapper(klass, code));
			}
		} catch (ClassNotFoundException e) {
			throw new CompilerException("error compiling class " + e);
		}

		this.classesMap = Collections.unmodifiableMap(classesMap);
	}

	private void deltreeOnExit(File dir) {
		for (File f : dir.listFiles()) {
			f.deleteOnExit();
			if (f.isDirectory())
				this.deltreeOnExit(f);
		}
	}

	public File getTargetDir() {
		return targetDir;
	}





}
