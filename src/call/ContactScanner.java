package call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactScanner implements Runnable {

	private static final List<String> hostsDefault = Arrays.asList(Config.DEFAULT_CONTACT_HOSTS);
	private static Set<String> hostsOfInterest;
	private static Set<String> hostsMostSeen;
	private static boolean active;
	private static Thread scannerThread;

	public ContactScanner() {
		if (hostsOfInterest == null) {
			hostsOfInterest = new HashSet<>();
			String[] hosts = Util.split(";", Config.CUSTOM_CONTACTS.getStringValue());
			hostsOfInterest.addAll(Util.asSet(hosts));
		}
		if (hostsMostSeen == null) {
			hostsMostSeen = new HashSet<>();
			String[] hosts = Util.split(";", Config.CONNECTED_CONTACTS.getStringValue());
			hostsMostSeen.addAll(Util.asSet(hosts));
		}
	}

	@Override
	public void run() {
		scannerThread = Thread.currentThread();
		active = true;
		while (active) {
			boolean hasBeenInterrupted = scan();
			if (!hasBeenInterrupted) {
				for (int i = 0; i < 30 && !Thread.interrupted(); ++i) {
					Util.sleep(1000);
				}
			}
		}
	}

	public static void scanNow() {
		scannerThread.interrupt();
	}

	private static boolean scan() {
		List<Thread> threads = new ArrayList<>();
		for (String host : hostsMostSeen) {
			threads.add(scan(host));
		}
		threads.add(scan(hostsDefault));
		threads.add(scan(hostsOfInterest));

		boolean interrupted = Util.joinThreads(threads);
		if (interrupted) {
			Util.interruptThreads(threads);
		}
		threads = null;
		System.gc();
		return interrupted;
	}

	private static Thread scan(final String host) {
		return scan(Arrays.asList(new String[] { host }));
	}

	private static Thread scan(final Collection<String> hosts) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i <= 5; ++i) {
						for (String host : hosts) {
							checkContact(host, Config.DEFAULT_PORT + 10 * i);
						}
					}
				} catch (InterruptedException e) {
					Util.log(Thread.currentThread().toString(), "interrupted...");
				}
			}
		});
		thread.start();
		return thread;
	}

	private static void checkContact(final String host, final int port) throws InterruptedException {
		if (Thread.interrupted()) // Clears interrupted status!
			throw new InterruptedException();

		try {
			StatusClient client = new StatusClient(host, port);
			client.close();
			ContactList.addContact(client.getContact());
			ContactList.setOnline(client.getContact(), true);
			addHostMostSeen(client.getContact().getHost());
		} catch (Exception e) {}
	}

	private static void addHostMostSeen(String host) {
		if (!hostsMostSeen.contains(host)) {
			hostsMostSeen.add(host);
			Config.CONNECTED_CONTACTS.setStringValue(Util.join(hostsMostSeen, ";"));
		}
	}

	public static void addHostOfInterest(String host) {
		if (!hostsDefault.contains(host)) {
			hostsOfInterest.add(host);
			Config.CUSTOM_CONTACTS.setStringValue(Util.join(hostsOfInterest, ";"));
		}
	}

	public void close() {
		active = false;
	}

}
