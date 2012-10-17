package org.gtc.gui;

import static org.gtc.util.Util.listFilesRecursive;

import japa.parser.ParseException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.compiler.DuplicatedCodeException;
import org.gtc.sourcecode.SourceCode;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;

public class Step1 extends JPanel {

	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private JButton btnLoad;

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

		rootNode = new DefaultMutableTreeNode("Master Code");
		treeModel = new DefaultTreeModel(rootNode, true);

		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setFont(new Font("Dialog", Font.PLAIN, 16));
		scrollPane.setViewportView(tree);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCodeInTree(e);
			}
		});
		btnLoad.setToolTipText("Load code from a src folder");
		panel.add(btnLoad);
	}

	private void loadCodeInTree(ActionEvent evt) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Choose a src folder");

		// If any directory was selected
		if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		// Compile files
		Compiler compiler = null;
		try {
			compiler = new Compiler();
		} catch (IOException e) {
			errorMessage("Error", e);
			return;
		}

		File selectedDir = fileChooser.getSelectedFile();
		File[] javaFiles = listFilesRecursive(selectedDir, "java");
		for (File file : javaFiles) {
			SourceCode sourceCode = null;
			try {
				sourceCode = new SourceCode(file);
				compiler.addCodes(sourceCode);
			} catch (ParseException e) {
				errorMessage("Parser Error", e);
				return;
			} catch (DuplicatedCodeException e) {
				errorMessage("Error", e);
				return;
			} catch (FileNotFoundException e) {
				// Probably never caught
				errorMessage("Unexpected Error", e);
				return;
			}
		}

		OutputStream output = new ByteArrayOutputStream();
		try {
			compiler.compile(new PrintStream(output));
		} catch (CompilerException e) {
			// TODO: Replace by a dialog that show errors in a JTextPane
			errorMessage("Error", e + "\n" + output);
			return;
		}

		// Use information from compiled classes to populate the tree
		Map<String, ClassWrapper> classes = compiler.getClasses();
		for (ClassWrapper cw : classes.values()) {
			// New node with qualified class name TODO: Try to put one node only for packages, similarly to eclipse
			DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(cw.getName());
			// Insert into class node it methods
			for (Method method : cw.getDeclaredPublicMethods())
				classNode.add(new DefaultMutableTreeNode(methodSignature(method), false));

			rootNode.add(classNode);
			treeModel.reload(rootNode);
		}

		// Some UI actions
		tree.expandRow(0);
		btnLoad.setEnabled(false);
	}

	private void errorMessage(String title, Object message) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private String methodSignature(Method method) {
		StringBuffer buffer = new StringBuffer(method.getName());
		Class<?>[] types = method.getParameterTypes();

		buffer.append("(");
		if (types.length > 0) {
			buffer.append(formattedTypeName(types[0]));
			for (int i = 1; i < types.length; i++)
				buffer.append(", " + formattedTypeName(types[i]));
		}
		buffer.append(")");
		buffer.append(": ");

		buffer.append(formattedTypeName(method.getReturnType()));

		return buffer.toString();
	}

	private String formattedTypeName(Class<?> type) {
		String name = type.getName();
		if (name.startsWith("java.lang."))
			return name.replaceFirst("java.lang.", "");

		return name;
	}

	protected void resetUI() {
		// Restore tree
		rootNode.removeAllChildren();
		treeModel.reload(rootNode);

		// Enable load button
		btnLoad.setEnabled(true);
	}

}
