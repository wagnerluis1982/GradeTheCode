package org.gtc.gui;

import static org.gtc.util.Util.listFilesRecursive;

import japa.parser.ParseException;

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
import org.gtc.sourcecode.SourceCode;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Step2 extends JPanel {

	private static final FileNameExtensionFilter JAVA_SOURCE_FILTER =
			new FileNameExtensionFilter("Java Source Code", "java");

	private MainWindow window;

	private JList<File> list;
	private DefaultListModel<File> listModel;

	/**
	 * Create the panel.
	 * @param window
	 */
	public Step2(MainWindow window) {
		this.window = window;

		setLayout(new BorderLayout(0, 0));

		JLabel lblMessage = new JLabel("<html>" +
				"<h1>Step 2 - Test Code</h1>" +
				"Add files to be used as test code for the project.<br/><br/>" +
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
		btnAddFolder.setToolTipText("Add one folder with java source files");
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
			openFilesChooser = window.getFileChooser();
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
			openDirChooser = window.getFileChooser();
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

	protected SourceCode[] getTestSourceCodes() throws ParseException, FileNotFoundException {
		List<SourceCode> sourceCodes = new ArrayList<SourceCode>();

		// Associate each File to a new SourceCode
		Enumeration<File> fileEnum = listModel.elements();
		while (fileEnum.hasMoreElements()) {
			File file = fileEnum.nextElement();
			try {
				sourceCodes.add(new SourceCode(file));
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException(
						String.format("test file \"%s\" not found", file));
			}
		}

		return sourceCodes.toArray(new SourceCode[0]);
	}

	protected void resetUI() {
		listModel.clear();
	}

}
