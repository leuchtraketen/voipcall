package call;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server implements Runnable, Activatable {

	private boolean listening = false;

	@Override
	public void run() {
		listening = true;
		while (listening) {
			ServerSocket serverSocket = null;

			CallConfig.CURRENT_PORT = CallConfig.DEFAULT_PORT;
			while (serverSocket == null) {
				try {
					serverSocket = new ServerSocket(CallConfig.CURRENT_PORT);
					System.out.println("Server listening on port: " + CallConfig.CURRENT_PORT);
				} catch (IOException e) {
					System.err.println("Could not listen on port: " + CallConfig.CURRENT_PORT + ".");
					serverSocket = null;
					Util.sleep(1000);
					CallConfig.CURRENT_PORT += 10;
				}
			}

			while (listening) {
				try {
					final Socket socket = serverSocket.accept();
					socket.setReuseAddress(true);

					SocketUtil.writeHeaders(socket.getOutputStream(), SocketUtil.RequestType.ServerCall);
					final InputStream instream = socket.getInputStream();
					final List<String> headers = SocketUtil.readHeaders(instream);

					final String remoteuser = SocketUtil.getHeaderValue(headers, "user");
					final String remotehost = socket.getInetAddress().getCanonicalHostName();

					final String request = SocketUtil.getHeaderValue(headers, "request");
					final Peer peer;
					if (request.toLowerCase().equals("status")) {
						// status connection
						socket.close();
						peer = null;

					} else if (request.toLowerCase().equals("call")) {
						socket.setSoTimeout(CallConfig.SOCKET_TIMEOUT);
						peer = Calls.registerPeer(socket, headers);
						Util.log(peer, "Connected (Server).");

					} else {
						Util.log(socket.toString(), "Fuck! Unknown connection type!");
						for (String header : headers) {
							Util.log(socket.toString(), "header: " + header);
						}
						peer = null;
					}

					new Thread(new Runnable() {
						@Override
						public void run() {
							Contact contact = Contacts.findContact(remotehost, remoteuser);
							if (contact == null) {
								contact = new Contact(remotehost, 0, remoteuser);
							}
							Contacts.addContact(contact);
							if (request.toLowerCase().equals("call")) {
								CallUi.openCall(contact);
								CallUi.setConnection(contact, peer);
							}
						}
					}).start();

				} catch (IOException e) {
					System.out.println("Error in call accept loop!");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean isActive() {
		return listening;
	}

	@Override
	public void close() {
		listening = false;
	}

	@Override
	public String toString() {
		return "0.0.0.0:" + CallConfig.CURRENT_PORT;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return hashCode() == obj.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
