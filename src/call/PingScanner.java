package call;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PingScanner implements Runnable {

	private static final int MIN_WAITTIME = 3000;
	private static final int MAX_WAITTIME = 30000;

	private static int waittime;
	private static boolean active;

	@Override
	public void run() {
		waittime = MIN_WAITTIME;
		active = true;
		while (active) {
			scan();
			if (waittime < MAX_WAITTIME)
				waittime += 2000;
			
			for (int i = 0; i < waittime; i += 1000) {
				Util.sleep(1000);
			}
		}
	}

	public static void resetWaitTime() {
		waittime = MIN_WAITTIME;
	}

	private static void scan() {
		for (Contact contact : ContactList.getUnsortedContacts()) {
			scan(contact);
		}
	}

	private static void scan(Contact contact) {
		if (contact.isReachable()) {
			try {
				PingClient client;
				client = new PingClient(contact);
				client.run();
				ContactList.setOnline(contact, true);
			} catch (UnknownHostException | SocketException e) {
				ContactList.setOnline(contact, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		active = false;
	}

}
