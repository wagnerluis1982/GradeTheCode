package org.gtc.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import org.gtc.gui.components.CList;
import org.gtc.gui.util.Dialogs;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Step3 extends JPanel {

	private JList<File> list;
	private DefaultListModel<File> listModel;
	private JFileChooser openDirChooser;
	private Dialogs dialogs = new Dialogs(this);

	/**
	 * Create the panel.
	 */
	public Step3() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblstep = new JLabel("<html>" +
				"<h1>Step 3 - Entrants Code</h1>" +
				"Add source code folders with entrants source code.<br/><br/>" +
				"Each folder name is used to identify the entrant on the result " +
				"screen.<br/><br/>" +
				"</html>");
		add(lblstep, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		listModel = new DefaultListModel<File>();
		list = new CList<File>(listModel);
		scrollPane.setViewportView(list);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addSourceFolders(evt);
			}
		});
		btnAdd.setToolTipText("Add one or more folders with the entrants code.");
		panel.add(btnAdd);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startGrade(evt);
			}
		});
		btnStart.setToolTipText("Start the grade process");
		panel.add(btnStart);

	}

	private void addSourceFolders(ActionEvent evt) {
		if (openDirChooser == null) {
			openDirChooser = new JFileChooser();
			openDirChooser.setMultiSelectionEnabled(true);
			openDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			openDirChooser.setDialogTitle("Choose source code folders");
		}

		// If no files selected
		if (openDirChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		for (File file : openDirChooser.getSelectedFiles())
			listModel.addElement(file);
	}

	private void startGrade(ActionEvent evt) {
		dialogs.warningMessage("Warning", "Not yet implemented");
	}

}
