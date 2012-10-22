package org.gtc.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class Step3 extends JPanel {

	/**
	 * Create the panel.
	 */
	public Step3() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblstep = new JLabel(
				"<html><h1>Step 3 - Entrants Code</h1></html>");
		add(lblstep, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

	}

}
