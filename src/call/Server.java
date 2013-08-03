package call;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends AbstractConnection implements Runnable {

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
					socket.setReuseAddress(true);

					SocketUtil.writeHeaders(socket.getOutputStream(), SocketUtil.RequestType.ServerCall);
					final InputStream instream = socket.getInputStream();
					final List<String> headers = SocketUtil.readHeaders(instream);

					final String remoteuser = SocketUtil.getHeaderValue(headers, "user");
					final String remotehost = socket.getInetAddress().getCanonicalHostName();

					Contact contact = ContactScanThread.findContact(remotehost, remoteuser);
					if (contact == null) {
						System.out.println("No contact found for: " + remoteuser + "@" + remotehost);
						contact = new Contact(remotehost, socket.getPort(), remoteuser);
						contact.setReachable(false);
					}
					if (Config.UID_S.equals(SocketUtil.getHeaderValue(headers, "UID"))) {
						contact = new Contact(remotehost, socket.getPort(), remoteuser);
						contact.setReachable(false);
					}

					final String request = SocketUtil.getHeaderValue(headers, "request");
					if (request.toLowerCase().equals("status")) {
						// status connection
						socket.close();

					} else if (request.toLowerCase().equals("call")) {
						socket.setSoTimeout(Config.SOCKET_TIMEOUT);
						if (!contact.isReachable()) {
							ContactScanThread.addContact(contact);
						}
						CallThread call = CallFactory.createCall(contact, socket, headers);
						new Thread(call).start();
						Util.log(call, "Connected (Server).");

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

	@Override
	public boolean isConnected() {
		return listening;
	}

	@Override
	public void close() {
		listening = false;
		super.close();
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
