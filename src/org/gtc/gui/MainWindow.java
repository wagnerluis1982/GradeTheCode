package org.gtc.gui;

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
import org.gtc.compiler.DuplicateCodeException;
import org.gtc.gui.components.JToolbarButton;
import org.gtc.sourcecode.ClassNotDefinedException;
import org.gtc.sourcecode.EmptyCodeException;
import org.gtc.sourcecode.FileNotReadException;
import org.gtc.sourcecode.SourceCode;
import org.gtc.util.Util;

public class MainWindow {

	private JFrame mainFrame;
	private AboutDialog aboutDialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
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
	public MainWindow() {
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
		mntmNew.setEnabled(false);
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

		JPanel panel = new JPanel();
		mainFrame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		String message = "Define the basis classes to grade or optionally load it from the src directory";
		JLabel lblDefineTheExpected = new JLabel(message);
		lblDefineTheExpected.setToolTipText(message);
		panel.add(lblDefineTheExpected, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		mainFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerSize(5);
		splitPane.setDividerLocation(250);

		final JScrollPane leftPane = new JScrollPane();
		splitPane.setLeftComponent(leftPane);

		// Tree node root
		final DefaultTreeModel treeModel = new DefaultTreeModel(
				new DefaultMutableTreeNode("Basis Classes"));

		// Class toolbar
		final JToolBar classToolBar = new JToolBar();
		classToolBar.setFloatable(false);
		leftPane.setColumnHeaderView(classToolBar);

		JButton btnNewClass = new JToolbarButton("New class", false);
		classToolBar.add(btnNewClass);

		JButton btnEditClass = new JToolbarButton("Edit class", false);
		classToolBar.add(btnEditClass);

		JButton btnDelClass = new JToolbarButton("Del class", false);
		classToolBar.add(btnDelClass);

		// Method toolbar
		final JToolBar methodToolBar = new JToolBar();
		methodToolBar.setFloatable(false);

		JButton btnNewMethod = new JToolbarButton("New method", false);
		methodToolBar.add(btnNewMethod);

		JButton btnDelMethod = new JToolbarButton("Del method", false);
		methodToolBar.add(btnDelMethod);

		JButton btnEditMethod = new JToolbarButton("Edit method", false);
		methodToolBar.add(btnEditMethod);

		// Tree of classes and methods
		final JTree tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				boolean rootSelected = tree.getRowForPath(e.getPath()) == 0;
				leftPane.setColumnHeaderView(rootSelected ? classToolBar
						: methodToolBar);
			}
		});
		leftPane.setViewportView(tree);

		JPanel rightPane = new JPanel();
		splitPane.setRightComponent(rightPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		panel.add(toolBar, BorderLayout.CENTER);

		JButton btnLoadBasis = new JButton("Load basis");
		toolBar.add(btnLoadBasis);
		btnLoadBasis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// If any directory was selected
				if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
					// Compile files
					final Map<String, ClassWrapper> classes;
					try {
						File selectedFile = fileChooser.getSelectedFile();
						File[] javaFiles = Util.listFilesRecursive(selectedFile, "java");

						Compiler compiler = new Compiler();
						for (File javafile : javaFiles)
							compiler.addSourceCode(new SourceCode(javafile));

						classes = compiler.compile();
					} catch (FileNotReadException ex) {
						JOptionPane.showMessageDialog(mainFrame, "Could not read the file",
								"Error opening file", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (EmptyCodeException ex) {
						JOptionPane.showMessageDialog(mainFrame, "The file is empty",
								"Error opening file", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (ClassNotDefinedException ex) {
						JOptionPane.showMessageDialog(mainFrame, "A class was not defined",
								"Error opening file", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(mainFrame, "Temporary dir could not be created",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (DuplicateCodeException ex) {
						JOptionPane.showMessageDialog(mainFrame, ex.getMessage(),
								"Unexpected error", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (CompilerException ex) {
						JOptionPane.showMessageDialog(mainFrame, ex.getMessage(),
								"Unexpected error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Use compiled classes to populate the tree
					for (ClassWrapper cw : classes.values()) {
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cw.getName());

						for (Method method : cw.getDeclaredPublicMethods()) {
							StringBuffer buffer = new StringBuffer(method.getName());
							Class<?>[] types = method.getParameterTypes();

							buffer.append("(");
							if (types.length > 0) {
								buffer.append(types[0].getCanonicalName());
								for (int i = 1; i < types.length; i++)
									buffer.append(", " + types[i].getCanonicalName());
							}
							buffer.append(")");

							newNode.add(new DefaultMutableTreeNode(buffer));
						}

						treeModel.insertNodeInto(newNode, (MutableTreeNode) treeModel.getRoot(), 0);
					}
					tree.expandRow(0);
				}
			}
		});
		btnLoadBasis.setToolTipText("Load basis classes from a src folder");
	}
}
