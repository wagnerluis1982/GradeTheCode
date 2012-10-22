package org.gtc.gui;

import static org.gtc.util.Util.listFilesRecursive;

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

import org.gtc.gui.components.CList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class Step2 extends JPanel {

	private JList<File> list;
	private DefaultListModel<File> listModel;
	private JFileChooser openFilesChooser;
	private JFileChooser openDirChooser;

	/**
	 * Create the panel.
	 */
	public Step2() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblMessage = new JLabel("<html>" +
				"<h1>Step 2 - Test Code</h1>" +
				"Open files to be used as test code for the project.<br/><br/>" +
				"Test codes are java sources that call parts of the real code " +
					"(here named Master Code), making some asserts. The concept " +
					"is the same of JUnit but it's not needed to use the JUnit " +
					"methods, you shall use only the Java <kbd>assert</kbd> " +
					"keyword instead.<br/><br/>" +
				"NOTE: if you want to use JUnit files already written, you can, " +
					"but you need to add JUnit jar to classpath when run this " +
					"program.<br/><br/>" +
				"<html>");
		add(lblMessage, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		listModel = new DefaultListModel<File>();
		list = new CList<File>(listModel);

		scrollPane.setViewportView(list);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		JButton btnAddFiles = new JButton("Add files");
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addFiles(evt);
			}
		});

		JButton btnAddFolder = new JButton("Add folder");
		btnAddFolder.setMnemonic(KeyEvent.VK_P);
		btnAddFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addFolder(evt);
			}
		});
		panel.add(btnAddFolder);
		btnAddFiles.setToolTipText("Add one or more files to use as test files");
		btnAddFiles.setMnemonic(KeyEvent.VK_O);
		panel.add(btnAddFiles);

	}

	protected void addFiles(ActionEvent evt) {
		if (openFilesChooser == null) {
			openFilesChooser = new JFileChooser();
			openFilesChooser.setFileFilter(new FileNameExtensionFilter("Java Source Code", "java"));
			openFilesChooser.setMultiSelectionEnabled(true);
			openFilesChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			openFilesChooser.setDialogTitle("Choose java source files");
		}

		// If no files selected
		if (openFilesChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		// TODO: test, before, if files are valid Java Source (How? Just parsing or compiling?)
		for (File file : openFilesChooser.getSelectedFiles())
			listModel.addElement(file);
	}

	protected void addFolder(ActionEvent evt) {
		if (openDirChooser == null) {
			openDirChooser = new JFileChooser();
			openDirChooser.setMultiSelectionEnabled(false);
			openDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			openDirChooser.setDialogTitle("Choose a folder with java source files");
		}

		// If no dir selected
		if (openDirChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		File selectedDir = openDirChooser.getSelectedFile();
		File[] javaFiles = listFilesRecursive(selectedDir, ".java");

		// TODO: test, before, if files are valid Java Source (How? Just parsing or compiling?)
		for (File file : javaFiles)
			listModel.addElement(file);
	}

	public void resetUI() {
		listModel.clear();
	}

}
