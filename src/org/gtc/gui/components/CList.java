package org.gtc.gui.components;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import org.gtc.gui.util.Dialogs;

public class CList<T> extends JList<T> {
	Dialogs dialogs = new Dialogs(this.getRootPane());

	public CList(ListModel<T> dataModel) {
		super(dataModel);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
					removeFromList();
				}
			}
		});
	}

	protected void removeFromList() {
		if (dialogs.confirmMessage("Confirm to remove", "Do you really want to remove?"))
		{
			DefaultListModel<T> listModel = (DefaultListModel<T>) this.getModel();

			int lastSelected = this.getLeadSelectionIndex();
			int[] selected = this.getSelectedIndices();
			for (int i = selected.length - 1; i >= 0; i--)
				listModel.removeElementAt(selected[i]);
			this.setSelectedIndex(lastSelected);
			this.clearSelection();
		}
	}

}
