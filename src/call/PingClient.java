package call;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import call.SocketUtil.RequestType;

public class PingClient extends AbstractClient implements Runnable {

	private static final String PING = "PING";
	private static final Map<Contact, Ping> pings = new HashMap<>();
	private static final Map<Contact, List<Listener>> listeners = new HashMap<>();

	private final PrintWriter out;
	private final BufferedReader in;
	private final boolean isServer;

	public PingClient(String host, int port) throws UnknownHostException, IOException {
		super(host, port, RequestType.Ping);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		isServer = false;
	}

	public PingClient(Contact contact) throws UnknownHostException, IOException {
		super(contact, RequestType.Ping);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		isServer = false;
	}

	public PingClient(Contact contact, Socket socket, List<String> headers) throws IOException {
		super(contact, socket, headers, RequestType.Ping);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		isServer = true;
	}

	@Override
	public void run() {
		long average = 0;
		long times = 0;
		long best = 0;
		long worst = 0;
		try {
			for (int i = 0; i < 20 && !socket.isClosed(); ++i) {
				long ms = ping(i == 0);
				if (ms > 0) {
					++times;
					average += ms;
					if (ms > worst || worst == 0)
						worst = ms;
					if (ms < best || best == 0)
						best = ms;
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (times > 0) {
			average = (long) ((double) (average) / (double) (times));
			long uptime = Long.parseLong(SocketUtil.getHeaderValue(headers, "uptime"));
			Ping ping = new Ping(average, best, worst, uptime);
			pings.put(contact, ping);
			notifyListeners(contact);
			//Util.msg(contact).println("Ping: " + ping.getId() + " ms", Color.PINK);
		}
	}

	private long ping(boolean isFirstPing) throws IOException {
		long starttime = System.currentTimeMillis();

		if (!(isFirstPing && isServer)) {
			// Util.log(contact, "written: ping");
			out.println(PING);
			out.flush();
		}

		String line;
		while (!socket.isClosed() && (line = in.readLine()) != null) {
			if (line.contains(PING)) {
				// Util.log(contact, "recieved: pong");
				long stoptime = System.currentTimeMillis();
				return stoptime - starttime;
			} else {
				Util.log(contact, "WTF?! " + line);
			}
		}
		return 0;
	}

	public static Ping getPing(Contact contact) {
		return pings.containsKey(contact) ? pings.get(contact) : null;
	}

	@Override
	public String getId() {
		return "PingClient<" + contact.getId() + ">";
	}

	private static void notifyListeners(Contact contact) {
		if (listeners.containsKey(contact)) {
			List<Listener> list = listeners.get(contact);
			for (Listener listener : list) {
				listener.onPingUpdate(contact);
			}
		}
	}

	public static void addListener(Contact contact, Listener listener) {
		if (listeners.containsKey(contact)) {
			List<Listener> list = listeners.get(contact);
			list.add(listener);
			listeners.put(contact, list);
		} else {
			List<Listener> list = new ArrayList<>();
			list.add(listener);
			listeners.put(contact, list);
		}
		PingScanner.resetWaitTime();
	}

	public static interface Listener {
		void onPingUpdate(Contact contact);
	}
}
