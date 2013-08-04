package call.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import call.CallUi;
import call.Contact;

public class ContactMouseAdapter extends MouseAdapter {
	private final JList<Contact> contactlist;
	private final ContactListModel contactmodel;

	public ContactMouseAdapter(JList<Contact> peerlist, ContactListModel peermodel) {
		this.contactlist = peerlist;
		this.contactmodel = peermodel;

		peerlist.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() >= 1) {
			int index = contactlist.locationToIndex(mouseEvent.getPoint());
			if (index >= 0) {
				Contact c = contactmodel.getElementAt(index);
				// Util.msg(c).println(c, Color.blue, "Selected!");
				CallUi.openChat(c);
			}
		}
	}
}