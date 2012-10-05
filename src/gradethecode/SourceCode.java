package gradethecode;

import gradethecode.exceptions.SourceCodeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCode {

	private String code;
	private File file, sourceDir;

	public SourceCode(String code) {
		if (code == null)
			throw new IllegalArgumentException("code should not be null");

		this.code = code;
	}

	public SourceCode(InputStream stream) {
		try {
			this.code = new Scanner(stream).useDelimiter("\\A").next();
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("empty source code");
		}
	}

	public SourceCode(File file) {
		if (!file.canRead())
			throw new SourceCodeException("could not read file " + file);

		try {
			this.code = new Scanner(file).useDelimiter("\\A").next();
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("empty source code");
		} catch (FileNotFoundException e) {
			// this block exists for the code stay in conformity with jvm rules
		}

		this.setFile(file);
	}

	public SourceCode(String code, File dir) {
		this(code);
		this.setSourceDir(dir);
	}

	public SourceCode(InputStream stream, File dir) {
		this(stream);
		this.setSourceDir(dir);
	}

	public SourceCode(File file, File dir) {
		this(file);
		this.setSourceDir(dir);
	}

	public String getPackageName() {
		// regex to match package name
		Pattern pattern = Pattern.compile(
				// junk before the package name
				"(?:\\s|(?m)^\\s*//.*$|/\\*.*\\*/)*(?:\\bpackage\\b)" +
				"(?:\\s|(?m)^\\s*//.*$|/\\*.*\\*/)*" +
				// grouping the package name
				"([^;]*)" +
				// rest of the file
				"(?:\\s*;.*)", Pattern.DOTALL);

		Matcher matcher = pattern.matcher(code);
		if (matcher.matches())
			return matcher.group(1).replaceAll("\\s", "");

		return "";
	}

	private void setSourceDir(File dir) {
		if (!dir.isDirectory())
			throw new SourceCodeException(dir + " is not a directory");

		this.sourceDir = dir;
	}

	public File getSourceDir() {
		// when sourceDir is not set means that a temporary should be created
		if (this.sourceDir == null) {
			try {
				this.sourceDir = Files.createTempDirectory("gtc_", null).toFile();
				this.sourceDir.deleteOnExit();
			} catch (IOException e) {
				throw new SourceCodeException("could not create temporary file");
			}
		}

		return this.sourceDir;
	}

	private void setFile(File file) {
		if (!file.isFile())
			throw new SourceCodeException(file + " is not a file");

		this.file = file;
	}

	public File getFile() {
		// when file is not set means that a temporary should be created
		if (this.file == null) {
			try {
				this.file = File.createTempFile("gtc_", null);
				FileWriter writer = new FileWriter(this.file);
				writer.write(code);
				writer.close();

				this.file.deleteOnExit();
			} catch (IOException e) {
				throw new SourceCodeException("could not create temporary file");
			}
		}

		return this.file;
	}

	@Override
	public String toString() {
		return this.code;
	}

}
