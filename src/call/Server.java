package call;

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
					Util.log(this, "Connecting (Server).");
					socket.setReuseAddress(true);

					SocketUtil.writeHeaders(socket.getOutputStream(), SocketUtil.RequestType.ServerCall);
					final InputStream instream = socket.getInputStream();
					final List<String> headers = SocketUtil.readHeaders(instream);

					final String remoteuser = SocketUtil.getHeaderValue(headers, "user");
					final String remotehost = socket.getInetAddress().getCanonicalHostName();

					Contact contact;

					// loopback connection?
					if (Config.UID_S.equals(SocketUtil.getHeaderValue(headers, "UID"))) {
						contact = new Contact(remotehost, socket.getPort(), remoteuser,
								Contact.Reachability.LOOPBACK);
					}
					// normal connection
					else {
						contact = ContactList.findContact(remotehost, 0, remoteuser);
						if (contact == null) {
							contact = new Contact(remotehost, socket.getPort(), remoteuser,
									Contact.Reachability.UNREACHABLE);
							System.out.println("No contact found for: " + contact);
						}
					}

					final String request = SocketUtil.getHeaderValue(headers, "request");
					if (request.toLowerCase().equals("status")) {
						// status connection
						socket.close();

					} else if (request.toLowerCase().equals("call")) {
						socket.setSoTimeout(Config.SOCKET_TIMEOUT);
						if (!contact.isReachable()) {
							ContactList.addContact(contact);
						}
						CallThread call = CallFactory.createCall(contact, socket, headers);
						new Thread(call).start();
						Util.log(contact, "Connected (Server).");

					} else {
						Util.log(socket.toString(), "Fuck! Unknown connection type!");
						for (String header : headers) {
							Util.log(socket.toString(), "header: " + header);
						}
					}

				} catch (IOException e) {
					System.out.println("Error in call accept loop!");
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
}
