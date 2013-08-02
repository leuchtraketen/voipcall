package call;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Contacts implements Runnable, Activatable {

	private static boolean active;

	private static List<Contact> contacts = new ArrayList<Contact>();
	private static List<Listener> listeners = new ArrayList<Listener>();

	public Contacts() {}

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
		for (String host : CallConfig.DEFAULT_CONTACT_HOSTS) {
			scan(host);
		}
	}

	private static List<Thread> scan(String host) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 5; ++i) {
			threads.add(tryContact(host, CallConfig.DEFAULT_PORT + 10 * i));
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
					if (!containsContact(client)) {
						addContact(new Contact(client));
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
			if (contact.getHost().equals(host) && contact.getUser().equals(user)) {
				return contact;
			}
		}
		List<Thread> threads = scan(host);
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
		for (Contact contact : contacts) {
			if (contact.getHost().equals(host) && contact.getUser().equals(user)) {
				return contact;
			}
		}
		return null;
	}

	public static boolean containsContact(String host, int port, String user) {
		for (Contact contact : contacts) {
			if (contact.getHost().equals(host) && contact.getPort() == port && contact.getUser().equals(user)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsContact(Client client) {
		return containsContact(client.getHost(), client.getPort(), client.getUser());
	}

	private static void removeContact(String host, int port) {
		Contact found = null;
		for (Contact c : contacts) {
			if (c.getHost().equals(host) && c.getPort() == port) {
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
		new Thread(new Contacts()).start();
	}

	public static void addListener(Listener listener) {
		listeners.add(listener);
	}

	public static interface Listener {
		void update();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void close() {
		active = false;
	}

	public static Contact me() {
		return new Contact("127.0.0.1", CallConfig.CURRENT_PORT, Util.getUserName());
	}
}
