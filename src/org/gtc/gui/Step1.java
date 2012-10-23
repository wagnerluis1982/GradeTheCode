package org.gtc.gui;

import static org.gtc.util.Util.listFilesRecursive;
import static org.gtc.util.Util.defaultIfNull;

import japa.parser.ParseException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.compiler.DuplicatedCodeException;
import org.gtc.gui.stuff.ClassTreeNode;
import org.gtc.gui.stuff.MethodTreeNode;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class Step1 extends JPanel {

	private MainWindow window;

	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private JButton btnLoad;
	private JFileChooser openDirChooser;

	/**
	 * Create the panel.
	 * @param window
	 */
	public Step1(MainWindow window) {
		this.window = window;

		setLayout(new BorderLayout(0, 0));

		JLabel lblMessage = new JLabel("<html>" +
				"<h1>Step 1 - Master Code</h1>" +
				"Define an expected set of code to compound the master code.<br/>" +
				"The <em>master code</em> is the basis code used for grading " +
					"entrants code. Normally it is written by the professor, " +
					"instructor, etc.<br/><br/>" +
				"To define the master code, you need to <em>Load</em> the project " +
					"before all.<br/> After that, you can make some adjustments, " +
					"below, of what classes or methods shall be mandatory in the " +
					"step of grading entrants code. Remove what you don't want by " +
					"pressing <em>Delete</em> at selection.<br/><br/>" +
				"<html>");
		add(lblMessage, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		rootNode = new DefaultMutableTreeNode("Master Code");
		treeModel = new DefaultTreeModel(rootNode, true);

		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_DELETE)
					removeFromTree();
			}
		});
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setFont(new Font("Dialog", Font.PLAIN, 16));
		scrollPane.setViewportView(tree);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);

		btnLoad = new JButton("Load");
		btnLoad.setMnemonic(KeyEvent.VK_L);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCodeInTree(e);
			}
		});
		btnLoad.setToolTipText("Load code from a src folder");
		panel.add(btnLoad);
	}

	private void loadCodeInTree(ActionEvent evt) {
		if (openDirChooser == null) {
			openDirChooser = new JFileChooser();
			openDirChooser.setMultiSelectionEnabled(false);
			openDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			openDirChooser.setDialogTitle("Choose a src folder");
		}

		// If any directory wasn't selected
		if (openDirChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		// Compile files
		Compiler compiler = null;
		try {
			compiler = new Compiler();
		} catch (IOException e) {
			window.dialogs.errorMessage("Error", e);
			return;
		}

		File selectedDir = openDirChooser.getSelectedFile();
		File[] javaFiles = listFilesRecursive(selectedDir, ".java");
		for (File file : javaFiles) {
			SourceCode sourceCode = null;
			try {
				sourceCode = new SourceCode(file);
				compiler.addCodes(sourceCode);
			} catch (ParseException e) {
				window.dialogs.errorMessage("Parser Error", e);
				return;
			} catch (DuplicatedCodeException e) {
				window.dialogs.errorMessage("Error", e);
				return;
			} catch (FileNotFoundException e) {
				// Probably never caught
				window.dialogs.errorMessage("Unexpected Error", e);
				return;
			}
		}

		OutputStream output = new ByteArrayOutputStream();
		try {
			compiler.compile(new PrintStream(output));
		} catch (CompilerException e) {
			// TODO: Replace by a dialog that show errors in a JTextPane
			window.dialogs.errorMessage("Error", e + "\n" + output);
			return;
		}

		// Populate the tree
		Map<String, DefaultMutableTreeNode> packageNodes =
				new Hashtable<String, DefaultMutableTreeNode>();
		for (ClassWrapper cw : compiler.getClasses().values()) {
			// Class nodes in packages, similarly to what we se on Eclipse
			String pkgName = defaultIfNull(cw.getPackageName(), "(default package)");
			DefaultMutableTreeNode pkgNode = packageNodes.get(pkgName);
			if (pkgNode == null) {
				pkgNode = new DefaultMutableTreeNode(pkgName);
				packageNodes.put(pkgName, pkgNode);
			}

			// New node with the class name
			ClassTreeNode classNode = new ClassTreeNode(cw);
			// Insert into class node it methods
			for (Method method : cw.getDeclaredPublicMethods())
				classNode.add(new MethodTreeNode(method));

			pkgNode.add(classNode);
		}

		for (MutableTreeNode pkgNode : packageNodes.values())
			rootNode.add(pkgNode);

		treeModel.reload(rootNode);

		// Some UI actions
		tree.setRootVisible(true);
		tree.expandRow(0);
		btnLoad.setEnabled(false);
	}

	private void removeFromTree() {
		int selectionRow = tree.getLeadSelectionRow();
		if (selectionRow == 0) {
			window.dialogs.warningMessage("Warning", "You can't delete the root");
			return;
		} else if (selectionRow > 0
				&& window.dialogs.confirmMessage("Confirm to remove",
						"Do you really want to remove?")) {
			MutableTreeNode deletingNode = (MutableTreeNode) tree
					.getLeadSelectionPath().getLastPathComponent();
			TreeNode parentNode = deletingNode.getParent();
			deletingNode.removeFromParent();
			treeModel.reload(parentNode);
		}
	}

	protected SourceCode[] getMasterSourceCodes() {
		List<SourceCode> sourceCodes = new ArrayList<SourceCode>();

		// Walk over packages
		Enumeration<?> pkgEnum = rootNode.children();
		while (pkgEnum.hasMoreElements()) {
			TreeNode pkgNode = (DefaultMutableTreeNode) pkgEnum.nextElement();

			// Walk over classes
			Enumeration<?> classEnum = pkgNode.children();
			while (classEnum.hasMoreElements()) {
				ClassTreeNode classNode = (ClassTreeNode) classEnum.nextElement();
				sourceCodes.add(classNode.getClassWrapper().getSourceCode());
			}
		}

		return sourceCodes.toArray(new SourceCode[0]);
	}

	protected void resetUI() {
		// Restore tree
		rootNode.removeAllChildren();
		tree.setRootVisible(false);
		treeModel.reload(rootNode);

		// Enable load button
		btnLoad.setEnabled(true);
	}

}
