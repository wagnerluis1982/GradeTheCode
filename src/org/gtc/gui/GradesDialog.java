package org.gtc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GradesDialog extends JDialog {

	private static final FileNameExtensionFilter HTML_FILTER =
			new FileNameExtensionFilter("HTML files", "html", "htm");

	private JFileChooser saveFileChooser;

	private JTextPane messageText;

	public GradesDialog(Component parent, CharSequence html) {
		setTitle("Entrants Grades");
		setMinimumSize(new Dimension(640, 480));
		setLocationRelativeTo(parent);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnSave = new JButton("Save");
				btnSave.setToolTipText("Save results to a file");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						saveResults(evt);
					}
				});
				buttonPane.add(btnSave);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				messageText = new JTextPane();
				messageText.setContentType("text/html");
				messageText.setText("<html>" + html + "</html>");

				// This pane is not editable
				messageText.setEditable(false);
				scrollPane.setViewportView(messageText);
			}
		}
	}

	private void saveResults(ActionEvent evt) {
		if (saveFileChooser == null) {
			saveFileChooser = new JFileChooser();
			saveFileChooser.setFileFilter(HTML_FILTER);
			saveFileChooser.setMultiSelectionEnabled(true);
			saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			saveFileChooser.setDialogTitle("Choose the file that you want to put the result");
		}

		while (saveFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File savingFile = saveFileChooser.getSelectedFile();

			if (savingFile.exists()) {
				int overwrite = JOptionPane.showConfirmDialog(this,
						"The selected file already exists. Do you want to overwrite?",
						"Overwrite confirmation", JOptionPane.YES_NO_OPTION);
				if (overwrite != JOptionPane.YES_OPTION)
					continue;

				try {
					FileWriter fileWriter = new FileWriter(savingFile);
					fileWriter.write(messageText.getText());
					fileWriter.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Problem to save the result on this path", "Error",
							JOptionPane.ERROR_MESSAGE);
					continue;
				}

			}
		}
	}

}
