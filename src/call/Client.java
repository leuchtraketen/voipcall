package call;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client extends AbstractConnection implements Runnable {

	private final Contact contact;
	private Socket socket;

	private SocketUtil.RequestType request;
	private final List<String> headers;

	public Client(String host, int port, SocketUtil.RequestType request) throws UnknownHostException,
			IOException {
		this.request = request;

		// open socket
		this.socket = new Socket(InetAddress.getByName(host), port);
		socket.setReuseAddress(true);

		// write and read headers
		SocketUtil.writeHeaders(socket.getOutputStream(), request);
		InputStream instream = socket.getInputStream();
		this.headers = SocketUtil.readHeaders(instream);

		// create contact
		host = socket.getInetAddress().getCanonicalHostName();
		final String user = SocketUtil.getHeaderValue(headers, "User");
		this.contact = new Contact(host, port, user);

		// handle request
		if (!request.equals(SocketUtil.RequestType.Status)) {
			socket.setSoTimeout(Config.SOCKET_TIMEOUT);
			Util.log(this, "Connected (Client).");
		}
	}

	public static Client connect(String host, SocketUtil.RequestType request) throws UnknownHostException {
		Client client = null;
		int port = Config.DEFAULT_PORT;
		for (int i = 0; i < 10; i++) {
			try {
				client = new Client(host, port, request);
				break;

			} catch (NoRouteToHostException e) {
				// Util.out(host + ":" + port, "No route to host.");
			} catch (ConnectException e) {
				// Util.out(host + ":" + port, "Connection refused.");
			} catch (IOException e) {
				e.printStackTrace();
			}
			port += 10;
			client = null;
		}
		if (client == null) {
			throw new UnknownHostException("No open port: " + host);
		}
		return client;
	}

	public static Client connect(String host, int port, SocketUtil.RequestType request)
			throws UnknownHostException, NoRouteToHostException, ConnectException {
		try {
			Client client = new Client(host, port, request);
			return client;
		} catch (NoRouteToHostException e) {
			throw e;
			// Util.out(host + ":" + port, "No route to host.");
		} catch (ConnectException e) {
			throw e;
			// Util.out(host + ":" + port, "Connection refused.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnknownHostException("No open port: " + host);
		}
	}

	@Override
	public void run() {
		setConnected(true);
		if (request.equals(SocketUtil.RequestType.Call)) {
			CallThread call = CallFactory.createCall(contact, socket, headers);
			call.addCloseListener(this);
			this.addCloseListener(call);
			new Thread(call).start();
		}
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {}
		super.close();
	}

	@Override
	public String getId() {
		return "Client<" + contact.getId() + ">";
	}

	public Contact getContact() {
		return contact;
	}

	public Socket getSocket() {
		return socket;
	}

	public SocketUtil.RequestType getRequest() {
		return request;
	}

	public List<String> getHeaders() {
		return headers;
	}
}
