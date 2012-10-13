package org.gtc.gui.components;

import java.awt.Font;

import javax.swing.JButton;

public class CToolBarButton extends JButton {

	public CToolBarButton(String text) {
		super(text);

		this.setFont(new Font("Dialog", Font.BOLD, 10));
	}

	public CToolBarButton(String text, boolean enabled) {
		this(text);

		this.setEnabled(false);
	}
}
