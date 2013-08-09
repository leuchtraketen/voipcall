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
			ServerSocket serverSocket = null;

			Config.CURRENT_PORT = Config.DEFAULT_PORT;
			while (serverSocket == null) {
				try {
					serverSocket = new ServerSocket(Config.CURRENT_PORT);
					System.out.println("Server listening on port: " + Config.CURRENT_PORT);
				} catch (IOException e) {
					System.err.println("Could not listen on port: " + Config.CURRENT_PORT + ".");
					serverSocket = null;
					Util.sleep(1000);
					Config.CURRENT_PORT += 10;
				}
			}

			while (listening) {
				try {
					final Socket socket = serverSocket.accept();
					new Thread(new Acceptor(socket)).start();
				} catch (IOException e) {
					System.out.println("Error in call accept loop (class Server)!");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isConnected() {
		return listening;
	}

	public void close() {
		listening = false;
	}

	@Override
	public String toString() {
		return "0.0.0.0:" + Config.CURRENT_PORT;
	}

	@Override
	public String getId() {
		return toString();
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
