package org.gtc.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import org.gtc.gui.actions.DisposeAction;

public class CMessageDialog extends JDialog {

	private JTextPane messageText;
	private JLabel messageLabel;

	/**
	 * Create the dialog with empty titles
	 */
	public CMessageDialog() {
		this("", "");
	}

	/**
	 * Create the dialog with equal titles
	 */
	public CMessageDialog(String title) {
		this(title, title);
	}

	/**
	 * Create the dialog.
	 */
	public CMessageDialog(String windowTitle, String messageTitle) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle(windowTitle);
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
			messageLabel = new JLabel(messageTitle);
			messageLabel.setFont(new Font("Dialog", Font.BOLD, 18));
			panel.add(messageLabel);
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
				messageText = new JTextPane();

				// This pane is not editable
				messageText.setEditable(false);
				scrollPane.setViewportView(messageText);
			}
		}
	}

	/**
	 * @param in
	 * @param desc
	 * @throws IOException
	 * @see javax.swing.JEditorPane#read(java.io.InputStream, java.lang.Object)
	 */
	public void setMessageFrom(InputStream in, Object desc) throws IOException {
		messageText.read(in, desc);
	}

	/**
	 * @param in
	 * @param desc
	 * @throws IOException
	 * @see javax.swing.text.JTextComponent#read(java.io.Reader, java.lang.Object)
	 */
	public void setMessageFrom(Reader in, Object desc) throws IOException {
		messageText.read(in, desc);
	}

	/**
	 * @param t
	 * @see javax.swing.JEditorPane#setText(java.lang.String)
	 */
	public void setMessage(CharSequence t) {
		messageText.setContentType("text/plain");
		messageText.setText(t != null ? t.toString() : "");
	}

	public void setHtmlMessage(CharSequence t) {
		messageText.setContentType("text/html");
		messageText.setText(t != null ? t.toString() : "");
	}

	/**
	 * @param text
	 * @see javax.swing.JLabel#setText(java.lang.String)
	 */
	public void setMessageTitle(String text) {
		messageLabel.setText(text);
	}

}
