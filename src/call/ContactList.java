package call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContactList {

	private static Set<Contact> contacts = new HashSet<Contact>();
	private static List<Listener> listeners = new ArrayList<Listener>();
	private static Map<Contact, Boolean> online = new HashMap<Contact, Boolean>();
	private static Lock lock = new ReentrantLock();

	public ContactList() {}

	public static List<Contact> getContacts() {
		lock.lock();
		List<Contact> list = new ArrayList<Contact>(contacts);
		Collections.sort(list, new ContactListComparator());
		lock.unlock();
		return list;
	}

	public static void addContact(Contact contact) {
		lock.lock();
		if (!contacts.contains(contact)) {
			contacts.add(contact);
			notifyListeners();
		}
		lock.unlock();
	}

	public static void removeContact(Contact contact) {
		lock.lock();
		if (contacts.contains(contact)) {
			contacts.remove(contact);
			notifyListeners();
		}
		lock.unlock();
	}

	public static Contact findContact(String host, int port, String user) {
		for (Contact contact : contacts) {
			if ((host == null || contact.isHost(host)) && (port == 0 || contact.isPort(port))
					&& (user == null || contact.isUser(user))) {
				return contact;
			}
		}
		ContactScanner.addHostOfInterest(host);
		return null;
	}

	public static boolean containsContact(String host, int port, String user) {
		for (Contact contact : contacts) {
			if (contact.isHost(host) && contact.isPort(port) && contact.isUser(user)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsContact(Contact contact) {
		return contacts.contains(contact);
	}

	public static boolean isOnline(Contact contact) {
		return online.containsKey(contact) && online.get(contact);
	}

	public static void setOnline(Contact contact, boolean onlinestatus) {
		lock.lock();
		online.put(contact, onlinestatus);
		notifyListeners();
		lock.unlock();
	}

	public static void update() {
		lock.lock();
		notifyListeners();
		lock.unlock();
	}

	private static void notifyListeners() {
		List<Contact> list = new ArrayList<Contact>(ContactList.getContacts());
		Util.log("contactlist:", "--------");
		for (Contact c : list) {
			if (list.size() > 2) {
				Util.log("contactlist:", c.getId() + " comp(1) = " + c.compareTo(list.get(0))
						+ ", comp(2) = " + c.compareTo(list.get(1)) + ", equal(1) = " + c.equals(list.get(0))
						+ ", equal(2) = " + c.equals(list.get(1)));
			} else {
				Util.log("contactlist:", c.getId());
			}
		}
		Util.log("contactlist:", "________");

		for (Listener listener : listeners) {
			listener.update();
		}
	}

	public static void addListener(Listener listener) {
		listeners.add(listener);
	}

	public static Contact me() {
		return new Contact("127.0.0.1", Config.CURRENT_PORT, Util.getUserName());
	}

	public static interface Listener {
		void update();
	}
}
