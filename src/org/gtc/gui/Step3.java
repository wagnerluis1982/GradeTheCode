package org.gtc.gui;

import static org.gtc.util.Util.pathJoin;
import japa.parser.ParseException;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.compiler.DuplicatedCodeException;
import org.gtc.grade.Grader;
import org.gtc.gui.components.CList;
import org.gtc.gui.components.CMessageDialog;
import org.gtc.grade.GradeResult;
import org.gtc.sourcecode.SourceCode;
import org.gtc.test.ConformityRules;
import org.gtc.test.TestResult;
import org.gtc.test.TestRunner;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Step3 extends JPanel {

	private MainWindow window;

	private JList<File> list;
	private DefaultListModel<File> listModel;
	private JFileChooser openDirChooser;

	/**
	 * Create the panel.
	 * @param window
	 */
	public Step3(MainWindow window) {
		this.window = window;

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
				startGrading(evt);
			}
		});
		btnStart.setToolTipText("Start the grade process");
		panel.add(btnStart);

	}

	private void addSourceFolders(ActionEvent evt) {
		if (openDirChooser == null) {
			openDirChooser = window.newFileChooser();
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

	private void startGrading(ActionEvent evt) {
		// Reading master and test codes
		SourceCode[] masterCodes = window.step1.getMasterSourceCodes();
		SourceCode[] testCodes = null;
		try {
			testCodes = window.step2.getTestSourceCodes();
		} catch (ParseException e) {
			window.dialogs.errorMessage("Test file parsing error", e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			// Hardly will fall here
			window.dialogs.errorMessage("Test file not found", e.getMessage());
			return;
		}

		if (masterCodes.length == 0) {
			window.dialogs.errorMessage("No master code", "No master code was found");
			return;
		}

		if (testCodes.length == 0) {
			window.dialogs.errorMessage("No test code", "No test code was found");
		}

		// Compiling master and test codes together
		Compiler compiler;
		try {
			compiler = new Compiler();
			compiler.addCodes(masterCodes);
			compiler.addAssertionCodes(testCodes);
		} catch (IOException e) {
			window.dialogs.errorMessage("Error", e);
			return;
		} catch (DuplicatedCodeException e) {
			window.dialogs.errorMessage("Error", e);
			return;
		}

		OutputStream output = new ByteArrayOutputStream();
		try {
			compiler.compile(new PrintStream(output));
		} catch (CompilerException e) {
			CMessageDialog errorDialog = new CMessageDialog("Compile error", e.getMessage());
			errorDialog.useSmallTitleFont();
			errorDialog.setMessage(output.toString());
			errorDialog.setLocationRelativeTo(this);
			errorDialog.setVisible(true);
			return;
		}

		// Run tests on master code
		TestRunner testRunner = new TestRunner();
		List<TestResult> masterTestResults = new ArrayList<TestResult>();
		for (ClassWrapper testClass : compiler.getAssertionClasses())
			masterTestResults.add(testRunner.runTest(testClass));

		final ConformityRules conformityRules = window.step1.getConformityRules();
		Grader grader = new Grader(conformityRules, masterTestResults);

		if (listModel.size() == 0) {
			window.dialogs.errorMessage("No entrant code",
					"You need at least one entrant code to start the grade process");
			return;
		}

		// Grade each entrant in the list
		List<GradeResult> entrantsGrades = new ArrayList<GradeResult>();
		Enumeration<File> entrantsEnum = listModel.elements();
		while (entrantsEnum.hasMoreElements()) {
			File dir = entrantsEnum.nextElement();
			String entrantName = dir.getName();

			// Create SourceCode objects for this entrant
			List<SourceCode> codes = new ArrayList<SourceCode>();
			for (SourceCode code : masterCodes) {
				String basePath = dir.getAbsolutePath();
				String sourcePath = code.getName().replace('.', File.separatorChar) + ".java";

				File sourceFile = new File(pathJoin(basePath, "src", sourcePath));
				if (!sourceFile.exists())
					sourceFile = new File(pathJoin(basePath, sourcePath));

				try {
					codes.add(new SourceCode(sourceFile));
				} catch (FileNotFoundException e) {
					entrantsGrades.add(new GradeResult(entrantName, 0,
							String.format("ERROR: source code for class %s not found", code.getName())));
					codes.clear();
					break;
				} catch (ParseException e) {
					entrantsGrades.add(new GradeResult(entrantName, 0,
							String.format("ERROR: source code for class %s with parsing errors", code.getName())));
					codes.clear();
					break;
				}
			}

			// if codes empty some error occurred, so pass to next entrant
			if (codes.size() == 0)
				continue;

			try {
				compiler = new Compiler();
				compiler.addCodes(codes.toArray(new SourceCode[0]));
				compiler.addAssertionCodes(testCodes);
				compiler.compile(new PrintStream(output));
			} catch (IOException e) {
			} catch (CompilerException e) {
				entrantsGrades.add(new GradeResult(entrantName, 0,
						String.format("ERROR: compilation error\n%s", output)));
				continue;
			}

			GradeResult grade = grader.getGrade(entrantName, compiler.getClasses(), compiler.getAssertionClasses());
			entrantsGrades.add(grade);
		}

		// Contructing the result
		StringBuffer html = new StringBuffer("<html>");
		html.append("<h1>Grades</h1>");
		html.append("<table border='1'>")
			.append("<tr><th>Name</th><th>Grade</th><th>Notes</th></tr>");
		for (GradeResult grade : entrantsGrades)
			html.append(String.format("<tr><td>%s</td><td>%.2f</td><td><pre>%s</pre></td></tr>",
					grade.getName(), grade.getGrade(), grade.getNotes()));
		html.append("</table>")
			.append("</html>");

		GradesDialog gradesDialog = new GradesDialog(this, html);
		gradesDialog.setVisible(true);
	}

	protected void resetUI() {
		listModel.clear();
	}

}
