package call.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import call.Contact;
import call.ContactList;
import call.ContactList.Listener;

public class ContactsBar implements Listener {

	private final JList<Contact> peerlist;
	private final ContactListModel peermodel;
	private final JPanel panel;

	public ContactsBar() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		peermodel = new ContactListModel();
		peerlist = new JList<Contact>(peermodel);
		peerlist.setCellRenderer(new ContactListCellRenderer());

		// peer list
		new ContactMouseAdapter(peerlist, peermodel);

		ContactList.addListener(this);
	}

	public void addToWindow(JFrame window) {
		window.getContentPane().add(BorderLayout.WEST, getComponent());
	}

	public JComponent getComponent() {
		JScrollPane listPane = new JScrollPane(peerlist);
		panel.add(BorderLayout.NORTH, new JLabel("Contacts", JLabel.CENTER));
		panel.add(BorderLayout.CENTER, listPane);
		panel.setPreferredSize(new Dimension(180, 350));
		return panel;
	}

	@Override
	public void update() {
		panel.repaint();
	}

	public void setSelectedContact(Contact contact) {
		if (contact != null) {
			int index = peermodel.indexOfElement(contact);
			if (index >= 0 && peerlist.getSelectedIndex() != index) {
				peerlist.setSelectedIndex(index);
			}
		} else {
			peerlist.clearSelection();
		}
	}

}
