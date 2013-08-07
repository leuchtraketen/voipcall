package call;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactScanner implements Runnable {

	private static Set<String> hostsOfInterest;
	private static boolean active;
	private static boolean interrupt = false;

	public ContactScanner() {
		if (hostsOfInterest == null) {
			hostsOfInterest = new HashSet<>();
			String[] hosts = Util.split(";", Config.CUSTOM_CONTACTS.getStringValue());
			hostsOfInterest.addAll(Util.asSet(hosts));
		}
	}

	@Override
	public void run() {
		active = true;
		while (active) {
			interrupt = false;
			scan();
			for (int i = 0; i < 30 && !interrupt; ++i) {
				Util.sleep(1000);
			}
		}
	}

	public static void scanNow() {
		interrupt = true;
	}

	private static void scan() {
		for (String host : Config.DEFAULT_CONTACT_HOSTS) {
			if (interrupt)
				return;
			scan(host);
		}
		for (String host : hostsOfInterest) {
			if (interrupt)
				return;
			scan(host);
		}
	}

	private static List<Thread> scan(String host) {
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i <= 5; ++i) {
			threads.add(checkContact(host, Config.DEFAULT_PORT + 10 * i));
		}
		return threads;
	}

	private static Thread checkContact(final String host, final int port) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StatusClient client = new StatusClient(host, port);
					client.close();
					ContactList.addContact(client.getContact());
					ContactList.setOnline(client.getContact(), true);

				} catch (UnknownHostException | SocketException e) {
					// ignore
				} catch (Exception e) {
					Contact found = ContactList.findContact(host, port, null);
					if (found != null) {
						ContactList.setOnline(found, true);
						// ContactList.removeContact(found);
					}

				}
			}
		});
		thread.start();
		return thread;
	}

	public static void addHostOfInterest(String host) {
		hostsOfInterest.add(host);
		Config.CUSTOM_CONTACTS.setStringValue(Util.join(hostsOfInterest, ";"));
	}

	public void close() {
		active = false;
	}

}
