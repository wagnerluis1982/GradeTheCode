package org.gtc.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.KeyAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class Step2 extends JPanel {

	private JList<File> list;
	private DefaultListModel<File> listModel;
	private GuiUtil util = new GuiUtil(this);
	private JFileChooser openFileChooser;

	/**
	 * Create the panel.
	 */
	public Step2() {
		setLayout(new BorderLayout(0, 0));

		openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter("Java Source Code", "java"));
		openFileChooser.setMultiSelectionEnabled(true);
		openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		openFileChooser.setDialogTitle("Choose java source files");

		JLabel lblMessage = new JLabel("<html>" +
				"<h1>Step 2 - Test Code</h1>" +
				"Open files to be used as test code for the project.<br/><br/>" +
				"Test codes are java sources that call parts of the real code " +
					"(here named Master Code), making some asserts. The concept " +
					"is the same of JUnit but it's not needed to use the JUnit " +
					"methods, you can use the Java <kbd>assert</kbd> keyword " +
					"instead.<br/><br/>" +
				"NOTE: if you want to use JUnit files already written, you can, " +
					"but you need to add JUnit jar to classpath when run this " +
					"program.<br/><br/>" +
				"<html>");
		add(lblMessage, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		listModel = new DefaultListModel<File>();
		list = new JList<File>(listModel);
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
					removeFromList();
				}
			}
		});

		scrollPane.setViewportView(list);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openFile(evt);
			}
		});
		btnOpen.setToolTipText("Open one or more files to use as test files");
		btnOpen.setMnemonic(KeyEvent.VK_O);
		panel.add(btnOpen);

	}

	protected void openFile(ActionEvent evt) {
		// If any file wasn't selected
		if (openFileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		// TODO: test, before, if is a valid Java Source files (How? Just parsing or compiling?)
		for (File file : openFileChooser.getSelectedFiles())
			listModel.addElement(file);
	}

	protected void removeFromList() {
		int lastSelected = list.getLeadSelectionIndex();
		int[] selection = list.getSelectedIndices();
		for (int i = selection.length - 1; i >= 0; i--)
			listModel.removeElementAt(i);
		list.setSelectedIndex(lastSelected);
		list.clearSelection();
	}

	public void resetUI() {
		listModel.clear();
	}

}
