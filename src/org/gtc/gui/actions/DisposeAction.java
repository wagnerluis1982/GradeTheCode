package org.gtc.gui.actions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

public class DisposeAction extends AbstractAction implements ActionListener {
	Window window;

	public DisposeAction(Window window) {
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.dispose();
	}
}
