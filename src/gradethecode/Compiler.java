package gradethecode;

import gradethecode.exceptions.CompilerException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class Compiler {
	private File targetDir;
	private List<SourceCode> codes;
	private boolean deleteIncluding = false;

	public Compiler(File targetDir) throws IOException, CompilerException {
		if (targetDir == null) {
			targetDir = Files.createTempDirectory("gtc_").toFile();
			targetDir.deleteOnExit();
			this.deleteIncluding = true;
		} else if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new CompilerException("invalid target directory");

		this.targetDir = targetDir;
		this.codes = new ArrayList<SourceCode>();
	}

	public Compiler() throws IOException, CompilerException {
		this(null);
	}

	public boolean compile() {
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

		boolean success = compilerTask.call();
		if (!success) {
			for (Diagnostic<?> d : diagnostics.getDiagnostics())
				System.out.printf("Error on line %d in %s", d.getLineNumber(), d);
		}

		return success;
	}

	public boolean deleteDir(File dir, boolean including) {
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
			return dir.delete();

		return true;
	}

	public boolean dispose() {
		return this.deleteDir(this.targetDir, this.deleteIncluding);
	}

	public void addSourceCode(SourceCode ...sourceCodes) {
		for (SourceCode code : sourceCodes)
			this.codes.add(code);
	}

	public File getTargetDir() {
		return targetDir;
	}

	public List<Class<?>> loadClasses() throws MalformedURLException,
			ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader loader = new URLClassLoader(
				new URL[] {this.targetDir.toURI().toURL()});

		for (SourceCode code : this.codes) {
			classes.add(loader.loadClass(code.getQualifiedName()));
		}

		return Collections.unmodifiableList(classes);
	}

}
