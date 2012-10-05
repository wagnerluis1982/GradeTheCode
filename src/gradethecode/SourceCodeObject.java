package gradethecode;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class SourceCodeObject extends SimpleJavaFileObject {

	private String name;
	private String code;

	protected SourceCodeObject(String name, String code) {
		super(URI.create("string:///" + name.replaceAll("\\.", "/")
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.name = name;
		this.code = code;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return code;
	}

}
