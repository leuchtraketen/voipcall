package call;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends AbstractId implements Runnable {

	public Server() {}

	private boolean listening = false;

	@Override
	public void run() {
		listening = true;

		while (listening) {
			ServerSocket serverSocket1 = openServerSocket(Config.DEFAULT_PORT_STATUS);
			Config.CURRENT_PORT_STATUS = serverSocket1.getLocalPort();
			ServerSocket serverSocket2 = openServerSocket(Config.DEFAULT_PORT_CALL);
			Config.CURRENT_PORT_CALL = serverSocket2.getLocalPort();

			new Thread(new UPNPClient(new int[] { Config.CURRENT_PORT_STATUS, Config.CURRENT_PORT_CALL }))
					.start();

			Thread listen1 = new Thread(new Listener(this, serverSocket1));
			Thread listen2 = new Thread(new Listener(this, serverSocket2));
			listen1.start();
			listen2.start();
			Util.joinThreads(listen1, listen2);
		}
	}

	private ServerSocket openServerSocket(int port) {
		ServerSocket serverSocket = null;
		while (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(port);
				break;
			} catch (IOException e) {
				System.err.println("Could not listen on port: " + port + ".");
				serverSocket = null;
				Util.sleep(1000);
				port += 10;
			}
		}
		System.out.println("Server listening on port: " + port);
		return serverSocket;
	}

	public boolean isListening() {
		return listening;
	}

	public void close() {
		listening = false;
	}

	@Override
	public String toString() {
		return "0.0.0.0:" + Config.CURRENT_PORT_STATUS;
	}

	@Override
	public String getId() {
		return toString();
	}

	private static class Listener implements Runnable {

		private final Server server;
		private final ServerSocket serversocket;

		public Listener(Server server, ServerSocket serversocket) {
			this.server = server;
			this.serversocket = serversocket;
		}

		@Override
		public void run() {
			while (server.isListening()) {
				try {
					final Socket socket = serversocket.accept();
					new Thread(new Acceptor(socket)).start();
				} catch (IOException e) {
					System.out.println("Error in call accept loop (class Server.Listener)!");
					e.printStackTrace();
				}
			}
		}
	}

	private static class Acceptor implements Runnable {
		final Socket socket;

		public Acceptor(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				handle();
			} catch (IOException e) {
				System.out.println("Error in call accept loop (class Acceptor)!");
				e.printStackTrace();
			}
		}

		private void handle() throws IOException {
			socket.setReuseAddress(true);
			socket.setTcpNoDelay(true);

			SocketUtil.writeHeaders(socket.getOutputStream(), SocketUtil.RequestType.ServerCall);
			final InputStream instream = socket.getInputStream();
			final List<String> headers = SocketUtil.readHeaders(instream);

			final String remoteuser = SocketUtil.getHeaderValue(headers, "user");
			final String remotehost = socket.getInetAddress().getCanonicalHostName();

			Contact contact;

			// loopback connection?
			if (Config.UID_S.equals(SocketUtil.getHeaderValue(headers, "UID"))) {
				contact = new Contact(remotehost, socket.getPort(), remoteuser, Contact.Reachability.LOOPBACK);
			}
			// normal connection
			else {
				contact = ContactList.findContact(remotehost, 0, remoteuser);
				if (contact == null) {
					contact = new Contact(remotehost, socket.getPort(), remoteuser,
							Contact.Reachability.UNREACHABLE);
					// System.out.println("No contact found for: " +
					// contact);
				}
			}

			// handle request
			final String request = SocketUtil.getHeaderValue(headers, "request");
			if (request.toLowerCase().equals("status")) {
				// status connection
				socket.close();

			} else if (request.toLowerCase().equals("ping")) {
				// ping connection
				PingClient client = new PingClient(contact, socket, headers);
				new Thread(client).start();

			} else if (request.toLowerCase().equals("call")) {
				// call connection
				socket.setSoTimeout(Config.SOCKET_READ_TIMEOUT);
				if (!contact.isReachable()) {
					ContactList.addContact(contact);
				}
				CallClient client = new CallClient(contact, socket, headers);
				client.startCall();
				Util.msg(contact).println("Incoming call.", Color.green);
				Util.log(contact, "Connected to call (Server).");

			} else if (request.toLowerCase().equals("chat")) {
				// chat connection
				socket.setSoTimeout(Config.SOCKET_READ_TIMEOUT);
				if (!contact.isReachable()) {
					ContactList.addContact(contact);
				}
				ChatClient client = new ChatClient(contact, socket, headers);
				client.saveTo(new ChatCapture(contact));
				new Thread(client).start();
				Util.log(contact, "Connected tp chat (Server).");

			} else {
				// unknown connection
				Util.log(socket.toString(), "Fuck! Unknown connection type!");
				for (String header : headers) {
					Util.log(socket.toString(), "header: " + header);
				}
			}
		}
	}
}
