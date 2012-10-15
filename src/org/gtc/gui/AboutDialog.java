package org.gtc.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.gtc.gui.actions.DisposeAction;

public class AboutDialog extends JDialog {

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("About Grade The Code");
		getContentPane().setLayout(new BorderLayout());

		DisposeAction disposeAction = new DisposeAction(this);

		// Esc keystroke
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

		// Dispose on press Esc
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKey, "Escape");
		getRootPane().getActionMap().put("Escape", disposeAction);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		{
			JLabel lblGradeTheCode = new JLabel("Grade The Code v0.0.1");
			lblGradeTheCode.setFont(new Font("Dialog", Font.BOLD, 18));
			panel.add(lblGradeTheCode);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(disposeAction);
				buttonPane.add(closeButton);
				getRootPane().setDefaultButton(closeButton);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				// A pane for license text
				JTextPane licenseText = new JTextPane();

				// Read LICENSE file
				InputStream license = getClass().getResourceAsStream("LICENSE");
				if (license != null)
					try {
						licenseText.read(new InputStreamReader(license), "");
					} catch (IOException e) {}
				else
					licenseText.setText("Could not read licensing file");

				setMinimumSize(licenseText.getPreferredSize());

				// This pane is not editable
				licenseText.setEditable(false);
				scrollPane.setViewportView(licenseText);
			}
		}
	}

}
