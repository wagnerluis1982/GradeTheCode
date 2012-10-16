package org.gtc.gui;

import japa.parser.ParseException;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.compiler.DuplicatedCodeException;
import org.gtc.gui.components.CToolBarButton;
import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;

public class CopyOfMainWindow {

	private JFrame mainFrame;
	private AboutDialog aboutDialog;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JButton btnLoadExpected;
	private DefaultMutableTreeNode rootNode;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CopyOfMainWindow window = new CopyOfMainWindow();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CopyOfMainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame("Grade The Code v0.0.1");
		mainFrame.setSize(640, 480);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenu mnProject = new JMenu("Project");
		mnProject.setMnemonic(KeyEvent.VK_R);
		menuBar.add(mnProject);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newProject(e);
			}
		});
		mnProject.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.setEnabled(false);
		mnProject.add(mntmOpen);

		JSeparator separator = new JSeparator();
		mnProject.add(separator);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		mnProject.add(mntmQuit);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (aboutDialog == null) {
					aboutDialog = new AboutDialog();
					aboutDialog.setLocationRelativeTo(mainFrame);
				}

				aboutDialog.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);

		JPanel topPanel = new JPanel(new BorderLayout(0, 0));
		mainFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

		JLabel lblMsgExpectCode = new JLabel("Define the expected code " +
				"manually or optionally load it from the src directory");
		topPanel.add(lblMsgExpectCode, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		mainFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerSize(5);
		splitPane.setDividerLocation(250);

		final JScrollPane leftPane = new JScrollPane();
		splitPane.setLeftComponent(leftPane);

		// Class toolbar
		final JToolBar classToolBar = new JToolBar();
		classToolBar.setFloatable(false);
		leftPane.setColumnHeaderView(classToolBar);

		JButton btnNewClass = new CToolBarButton("New class", false);
		classToolBar.add(btnNewClass);

		JButton btnEditClass = new CToolBarButton("Edit class", false);
		classToolBar.add(btnEditClass);

		JButton btnDelClass = new CToolBarButton("Del class", false);
		classToolBar.add(btnDelClass);

		// Method toolbar
		final JToolBar methodToolBar = new JToolBar();
		methodToolBar.setFloatable(false);

		JButton btnNewMethod = new CToolBarButton("New method", false);
		methodToolBar.add(btnNewMethod);

		JButton btnDelMethod = new CToolBarButton("Del method", false);
		methodToolBar.add(btnDelMethod);

		JButton btnEditMethod = new CToolBarButton("Edit method", false);
		methodToolBar.add(btnEditMethod);

		rootNode = new DefaultMutableTreeNode("Expected Code");
		treeModel = new DefaultTreeModel(
				rootNode, true);

		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				int pathCount = e.getPath().getPathCount();
				leftPane.setColumnHeaderView(pathCount <= 2 ? classToolBar : methodToolBar);
			}
		});
		leftPane.setViewportView(tree);

		JPanel rightPane = new JPanel();
		splitPane.setRightComponent(rightPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		topPanel.add(toolBar, BorderLayout.CENTER);

		btnLoadExpected = new JButton("Load code");
		btnLoadExpected.setToolTipText("Load expected code from a src folder");
		toolBar.add(btnLoadExpected);
		btnLoadExpected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoadCodeActionPerformed(e);
			}
		});
	}

	protected void newProject(ActionEvent e) {
		rootNode.removeAllChildren();
		treeModel.reload(rootNode);
		tree.setRootVisible(false);
		btnLoadExpected.setEnabled(true);
	}

	private void btnLoadCodeActionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// If any directory was selected
		if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
			// Compile files
			Map<String, ClassWrapper> classes = null;
			try {
				File selectedFile = fileChooser.getSelectedFile();
				File[] javaFiles = Util.listFilesRecursive(selectedFile, "java");
				SourceCode[] codes = new SourceCode[javaFiles.length];
				for (int i = 0; i < javaFiles.length; i++)
					codes[i] = new SourceCode(javaFiles[i]);

				Compiler compiler = new Compiler(codes);
				compiler.compile();
				classes = compiler.getClasses();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(mainFrame, "Temporary dir could not be created",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (ParseException ex) {
			} catch (DuplicatedCodeException ex) {
			} catch (CompilerException ex) {
			}

			// Use compiled classes to populate the tree
			for (ClassWrapper cw : classes.values()) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cw.getName());

				for (Method method : cw.getDeclaredPublicMethods())
					newNode.add(new DefaultMutableTreeNode(
							methodSignature(method), false));

				treeModel.insertNodeInto(newNode, (MutableTreeNode) treeModel.getRoot(), 0);
			}
			tree.setRootVisible(true);
			tree.expandRow(0);
			btnLoadExpected.setEnabled(false);
		}
	}

	private String methodSignature(Method method) {
		StringBuffer buffer = new StringBuffer(method.getName());
		Class<?>[] types = method.getParameterTypes();

		buffer.append("(");
		if (types.length > 0) {
			buffer.append(prettyType(types[0]));
			for (int i = 1; i < types.length; i++)
				buffer.append(", " + prettyType(types[i]));
		}
		buffer.append(")");
		buffer.append(": ");

		buffer.append(prettyType(method.getReturnType()));

		return buffer.toString();
	}

	private String prettyType(Class<?> returnType) {
		String name = returnType.getName();
		if (name.startsWith("java.lang."))
			return name.replaceFirst("java.lang.", "");

		return name;
	}



}
