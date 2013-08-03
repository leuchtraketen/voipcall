package call.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import call.CallUi;
import call.Config;
import call.Contact;
import call.ContactList;
import call.ContactList.Listener;
import call.Util;

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
		new PeerMouseAdapter(peerlist, peermodel);
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

	public static class ContactListModel extends AbstractListModel<Contact> implements Listener {

		private static final long serialVersionUID = -3163098678792030738L;

		public ContactListModel() {
			ContactList.addListener(this);
		}

		public int getSize() {
			return ContactList.getContacts().size();
		}

		public Contact getElementAt(int index) {
			List<Contact> list = ContactList.getContacts();
			if (index > 0 && index < list.size()) {
				return list.get(index);
			} else if (list.size() > 0) {
				return list.get(0);
			} else {
				return new Contact("localhost", Config.CURRENT_PORT, "this instance");
			}
		}

		@Override
		public void update() {
			this.fireContentsChanged(this, 0, getSize());
		}
	}

	public static class PeerMouseAdapter extends MouseAdapter {
		private final JList<Contact> peerlist;
		private final ContactListModel peermodel;

		public PeerMouseAdapter(JList<Contact> peerlist, ContactListModel peermodel) {
			this.peerlist = peerlist;
			this.peermodel = peermodel;

			peerlist.addMouseListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() >= 1) {
				int index = peerlist.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Contact c = peermodel.getElementAt(index);
					Util.msg(c).println(c, Color.blue, "Selected!");
					CallUi.openChat(c);
				}
			}
		}
	}

}
