package call;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactList implements Runnable {

	private static boolean active;

	private static List<Contact> contacts = new ArrayList<Contact>();
	private static List<Listener> listeners = new ArrayList<Listener>();
	private static Set<String> runtimeContactHosts = new HashSet<String>();

	public ContactList() {}

	public static List<Contact> getContacts() {
		return contacts;
	}

	@Override
	public void run() {
		active = true;
		while (active) {
			scan();
			Util.sleep(30000);
		}
	}

	private static void scan() {
		for (String host : Config.DEFAULT_CONTACT_HOSTS) {
			scan(host);
		}
		for (String host : runtimeContactHosts) {
			scan(host);
		}
	}

	private static List<Thread> scan(String host) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 5; ++i) {
			threads.add(tryContact(host, Config.DEFAULT_PORT + 10 * i));
		}
		return threads;
	}

	private static Thread tryContact(final String host, final int port) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Client client = Client.connect(host, port, SocketUtil.RequestType.Status);
					client.close();
					System.out.println("abc" + client);
					if (!containsContact(client)) {
						addContact(client.getContact());
						Util.log(client, "Online");
					}
					update();
				} catch (SocketException e) {} catch (Exception e) {
					// Util.out(host + ":" + port, "Offline (" +
					// e.getLocalizedMessage() + ")");
					removeContact(host, port);
				}
			}
		});
		thread.start();
		return thread;
	}

	public static void addContact(Contact contact) {
		if (!contacts.contains(contact))
			contacts.add(contact);
	}

	private static void removeContact(Contact contact) {
		if (!contacts.contains(contact))
			contacts.remove(contact);
	}

	public static Contact findContact(String host, String user) {
		for (Contact contact : contacts) {
			if (contact.isHost(host) && contact.isUser(user)) {
				return contact;
			}
		}
		runtimeContactHosts.add(host);
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

	private static boolean containsContact(Client client) {
		return containsContact(client.getContact().getHost(), client.getContact().getPort(), client
				.getContact().getUser());
	}

	private static void removeContact(String host, int port) {
		Contact found = null;
		for (Contact c : contacts) {
			if (c.isHost(host) && c.isPort(port)) {
				found = c;
			}
		}
		if (found != null) {
			removeContact(found);
		}
	}

	private static void update() {
		for (Listener listener : listeners) {
			listener.update();
		}
	}

	public static void start() {
		new Thread(new ContactList()).start();
	}

	public static void addListener(Listener listener) {
		listeners.add(listener);
	}

	public static interface Listener {
		void update();
	}

	public void close() {
		active = false;
	}

	public static Contact me() {
		return new Contact("127.0.0.1", Config.CURRENT_PORT, Util.getUserName());
	}
}
