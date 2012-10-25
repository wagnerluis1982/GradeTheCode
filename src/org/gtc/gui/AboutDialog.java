package org.gtc.gui;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gtc.gui.components.CMessageDialog;
import org.gtc.util.Util;

public class AboutDialog extends CMessageDialog {

	/**
	 * Create the dialog.
	 */
	public AboutDialog() {
		super("About Grade The Code", "Grade The Code v0.0.1");

		// Read LICENSE file
		InputStream license = Util.class.getResourceAsStream("LICENSE");
		if (license != null)
			try {
				setMessageFrom(new InputStreamReader(license), "");
			} catch (IOException e) {
			}
		else
			setMessage("Could not read licensing file");

		setMinimumSize(new Dimension(550, 250));
	}

}
