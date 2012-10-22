package org.gtc.gui.stuff;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gtc.compiler.ClassWrapper;

public class ClassTreeNode extends DefaultMutableTreeNode {

	private ClassWrapper classWrapper;

	public ClassTreeNode(ClassWrapper classWrapper) {
		super(classWrapper.getSimpleName(), true);
		this.classWrapper = classWrapper;
	}

	public ClassWrapper getClassWrapper() {
		return classWrapper;
	}

}
