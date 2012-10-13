package org.gtc.gui.components;

import java.awt.Font;

import javax.swing.JButton;

public class JToolbarButton extends JButton {

	public JToolbarButton(String text) {
		super(text);

		this.setFont(new Font("Dialog", Font.BOLD, 10));
	}

	public JToolbarButton(String text, boolean enabled) {
		this(text);

		this.setEnabled(false);
	}
}
