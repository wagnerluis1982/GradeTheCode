package org.gtc.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class MainWindow {

	private JFrame mainFrame;
	private AboutDialog aboutDialog;
	private GuiUtil util = new GuiUtil(mainFrame);
	private Step1 step1;
	private Step2 step2;

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
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newProject(e);
			}
		});
		mntmNew.setMnemonic(KeyEvent.VK_R);
		mnProject.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.setEnabled(false);
		mnProject.add(mntmOpen);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitApplication(e);
			}
		});

				JSeparator separator = new JSeparator();
				mnProject.add(separator);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mnProject.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.setEnabled(false);
		mnProject.add(mntmSaveAs);

		JSeparator separator_1 = new JSeparator();
		mnProject.add(separator_1);
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mnProject.add(mntmQuit);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAboutDialog(e);
			}
		});
		mnHelp.add(mntmAbout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		step1 = new Step1();
		tabbedPane.addTab("Step 1", null, step1, null);

		step2 = new Step2();
		tabbedPane.addTab("Step 2", null, step2, null);
	}

	private void newProject(ActionEvent evt) {
		step1.resetUI();
		step2.resetUI();
	}

	private void showAboutDialog(ActionEvent evt) {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
			aboutDialog.setLocationRelativeTo(mainFrame);
		}

		aboutDialog.setVisible(true);
	}

	private void quitApplication(ActionEvent evt) {
		if (util.confirmMessage("Confirm", "Do you really want to quit?"))
			System.exit(0);
	}

}
