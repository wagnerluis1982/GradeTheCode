package org.gtc.gui.util;

import static javax.swing.JOptionPane.*;

import java.awt.Component;

public class Dialogs {

	private Component parent;

	public Dialogs(Component parent) {
		this.parent = parent;
	}

	public void errorMessage(String title, Object message) {
		showMessageDialog(parent, message, title, ERROR_MESSAGE);
	}

	public void warningMessage(String title, Object message) {
		showMessageDialog(parent, message, title, WARNING_MESSAGE);
	}

	public boolean confirmMessage(String title, Object message) {
		return showConfirmDialog(parent, message, title, YES_NO_OPTION) == YES_OPTION;
	}

}
