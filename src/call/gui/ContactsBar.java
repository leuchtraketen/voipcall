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

public class ContactsBar {

	private final JList<Contact> peerlist;
	private final ContactListModel peermodel;
	private final JPanel panel;

	public ContactsBar() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		peermodel = new ContactListModel();
		peerlist = new JList<Contact>(peermodel);

		// peer list
		new ContactMouseAdapter(peerlist, peermodel);
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

}
