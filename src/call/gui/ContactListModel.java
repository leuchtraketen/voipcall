package call.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import call.Config;
import call.Contact;
import call.ContactList;
import call.ContactList.Listener;

public class ContactListModel extends AbstractListModel<Contact> implements Listener {

	private static final long serialVersionUID = -3163098678792030738L;

	public ContactListModel() {
		ContactList.addListener(this);
	}

	public int getSize() {
		return ContactList.getSortedContacts().size();
	}

	public Contact getElementAt(int index) {
		List<Contact> list = new ArrayList<>(ContactList.getSortedContacts());
		if (index > 0 && index < list.size()) {
			return list.get(index);
		} else if (list.size() > 0) {
			return list.get(0);
		} else {
			return new Contact("localhost", Config.CURRENT_PORT, "this instance");
		}
	}

	public int indexOfElement(Contact contact) {
		List<Contact> list = new ArrayList<>(ContactList.getSortedContacts());
		for (int i = 0; i < list.size(); ++i) {
			if (contact.equals(list.get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onAnyContactUpdate() {
		this.fireContentsChanged(this, 0, getSize());
	}

	@Override
	public void onContactUpdate(Contact contact) {}
}
