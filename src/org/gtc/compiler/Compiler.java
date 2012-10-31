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

/**
 * The class {@code Compiler} is used to compile java files
 *
 * <p> It use is basically pass the input {@link SourceCode} objects and get
 * {@link java.lang.Class} objects as output.
 *
 * <p> You can use this class in the following way:
 * <pre>
 *     SourceCode code1 = new SourceCode(...);
 *     SourceCode code2 = new SourceCode(...);
 *
 *     Compiler compiler = new Compiler();
 *     compiler.addCodes(code1, code2);
 *     compiler.compile();
 *
 *     Map&lt;String, ClassWrapper&gt; classes = compiler.getClasses();
 * </pre>
 *
 * <p> The method {@link #getClasses()} returns a map where each key is the
 * fully qualified class name (e.g.: {@code java.lang.Class} instead of
 * {@code Class}).
 *
 * @author Wagner Macedo
 */
public class Compiler {
	private File targetDir;
	private Set<SourceCode> codes;
	private List<SourceCode> assertionCodes;
	private Map<String, ClassWrapper> classesMap;

	/**
	 * Create a compiler work
	 *
	 * @param targetDir the directory where the compiled files will stay
	 * @throws InvalidTargetException
	 */
	public Compiler(File targetDir) throws InvalidTargetException {
		if (!(targetDir.isDirectory() && targetDir.canWrite()))
			throw new InvalidTargetException("invalid target directory");

		initialize(targetDir);
	}

	/**
	 * Create a compiler work using a newly created temporary directory
	 *
	 * @throws IOException
	 */
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

	/**
	 * Add {@link SourceCode} objects to this compiler work
	 *
	 * @param codes array of {@code SourceCode} objects
	 */
	public void addCodes(SourceCode... codes) {
		codes = Util.filterNonNull(codes);

		for (SourceCode code : codes) {
			if (this.codes.contains(code))
				throw new DuplicatedCodeException("the class " + code.getName() +
						" is already in the set");
			this.codes.add(code);
		}
	}

	/**
	 * Add {@link SourceCode} objects to this compiler work to be loaded after
	 * compilation with {@code enableassertions} flag, suitable to be used as
	 * test codes.
	 *
	 * @param codes array of {@code SourceCode} objects
	 */
	public void addAssertionCodes(SourceCode... codes) {
		this.addCodes(codes);
		this.assertionCodes.addAll(Arrays.asList(codes));
	}

	/**
	 * Do the compilation in the previously added {@link SourceCode} objects
	 *
	 * @param out {@link PrintStream} object to be used for showing compiler
	 * messages. If {@code null}, {@code System.out} is used.
	 * @throws CompilerException
	 */
	public void compile(PrintStream out) throws CompilerException {
		if (this.codes.size() == 0)
			throw new NoSourceCodesException("No source codes found");

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

	/**
	 * Do the compilation in the previously added {@link SourceCode} objects,
	 * using {@code System.out} as the {@code PrintStream}.
	 *
	 * @throws CompilerException
	 */
	public void compile() throws CompilerException {
		this.compile(null);
	}

	/**
	 * Get classes loaded with {@code enableassertions} flag enabled
	 *
	 * @return array of {@link ClassWrapper} objects
	 */
	public ClassWrapper[] getAssertionClasses() {
		List<ClassWrapper> classes = new ArrayList<ClassWrapper>();
		for (SourceCode code : this.assertionCodes)
			classes.add(this.classesMap.get(code.getName()));

		return classes.toArray(new ClassWrapper[0]);
	}

	/**
	 * Get a map of loaded classes after compilation job. The map keys are the
	 * fully qualified class names.
	 *
	 * @return {@code Map} of {@link ClassWrapper} objects
	 */
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

	/**
	 * Get the target dir set to this compiler work
	 *
	 * @return the target dir
	 */
	public File getTargetDir() {
		return targetDir;
	}

}
