package org.gtc.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Step1 extends JPanel {

	/**
	 * Create the panel.
	 */
	public Step1() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("<html>" +
				"<h1>Step 1 - Master Code</h1>" +
				"Define an expected set of code to compound the master code.<br/>" +
				"The <em>master code</em> is the basis code used for grading " +
					"entrants code. Normally it is written by the professor, " +
					"instructor, etc.<br/><br/>" +
				"To define the master code, you need to <em>Load</em> the project " +
					"before all.<br/> After that, you can make some adjustments, " +
					"below, of what classes or methods shall be mandatory in the " +
					"step of grading entrants code.<br/><br/>" +
				"<html>");
		add(lblNewLabel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		JTree tree = new JTree();
		tree.setFont(new Font("Dialog", Font.PLAIN, 16));
		scrollPane.setViewportView(tree);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMasterCode(e);
			}
		});
		btnLoad.setToolTipText("Load code from a src folder");
		panel.add(btnLoad);
	}

	private void loadMasterCode(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// If any directory was selected
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		}
	}

}
