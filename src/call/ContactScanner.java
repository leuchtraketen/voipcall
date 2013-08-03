package call;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactScanner implements Runnable {

	private static Set<String> hostsOfInterest = new HashSet<String>();
	private static boolean active;

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
		for (String host : hostsOfInterest) {
			scan(host);
		}
	}

	private static List<Thread> scan(String host) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 5; ++i) {
			threads.add(checkContact(host, Config.DEFAULT_PORT + 10 * i));
		}
		return threads;
	}

	private static Thread checkContact(final String host, final int port) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Client client = Client.connect(host, port, SocketUtil.RequestType.Status);
					client.close();
					System.out.println("abc " + client);
					ContactList.addContact(client.getContact());
					ContactList.setOnline(client.getContact(), true);
					
				} catch (SocketException e) {} catch (Exception e) {					
					Contact found = ContactList.findContact(host, port, null);
					if (found != null) {
						ContactList.setOnline(found, true);
						//ContactList.removeContact(found);
					}
				}
			}
		});
		thread.start();
		return thread;
	}

	public static void addHostOfInterest(String host) {
		hostsOfInterest.add(host);
	}

	public void close() {
		active = false;
	}

}
