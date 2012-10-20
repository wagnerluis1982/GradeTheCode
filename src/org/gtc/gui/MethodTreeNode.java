package org.gtc.gui;

import java.lang.reflect.Method;

import javax.swing.tree.DefaultMutableTreeNode;

public class MethodTreeNode extends DefaultMutableTreeNode {

	private Method method;

	public MethodTreeNode(Method method) {
		super(methodSignature(method), false);
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	private static String methodSignature(Method method) {
		StringBuffer buffer = new StringBuffer(method.getName());
		Class<?>[] types = method.getParameterTypes();

		buffer.append("(");
		if (types.length > 0) {
			buffer.append(formattedTypeName(types[0]));
			for (int i = 1; i < types.length; i++)
				buffer.append(", " + formattedTypeName(types[i]));
		}
		buffer.append(")");
		buffer.append(": ");

		buffer.append(formattedTypeName(method.getReturnType()));

		return buffer.toString();
	}

	private static String formattedTypeName(Class<?> type) {
		String name = type.getCanonicalName();
		if (name.matches("java\\.lang\\.\\w*(?:\\[\\])?"))
			return name.replaceFirst("java.lang.", "");

		return name;
	}

}
