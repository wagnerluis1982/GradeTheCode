package org.gtc.gui;

import static javax.swing.JOptionPane.*;

import java.awt.Component;

public class GuiUtil {

	private Component c;

	public GuiUtil(Component c) {
		this.c = c;
	}

	public void errorMessage(String title, Object message) {
		showMessageDialog(c, message, title, ERROR_MESSAGE);
	}

	public void warningMessage(String title, Object message) {
		showMessageDialog(c, message, title, WARNING_MESSAGE);
	}

	public boolean confirmMessage(String title, Object message) {
		return showConfirmDialog(c, message, title, YES_NO_OPTION) == YES_OPTION;
	}

}
