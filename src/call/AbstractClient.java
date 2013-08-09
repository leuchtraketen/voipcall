package call;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import call.SocketUtil.RequestType;

public abstract class AbstractClient extends AbstractId {

	private final RequestType request;
	protected Socket socket;
	protected Contact contact;
	protected List<String> headers;

	public AbstractClient(String host, int port, RequestType request) throws IOException {
		this.request = request;
		init(host, port);
	}

	public AbstractClient(Contact contact, RequestType request) throws IOException {
		this.request = request;
		init(contact.getHost(), contact.getPort());
	}

	public AbstractClient(Contact contact, Socket socket, List<String> headers, RequestType request) {
		this.request = request;
		this.contact = contact;
		this.socket = socket;
		this.headers = headers;
	}

	private void init(String host, int port) throws IOException {
		try {
			connect(host, port);
		} catch (UnknownHostException | SocketTimeoutException | SocketException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnknownHostException("No open port: " + host);
		}
	}

	private void connect(String host, int port) throws IOException {
		// open socket
		SocketAddress addr = new InetSocketAddress( host, port );
		this.socket = new Socket();
		socket.connect( addr, Config.SOCKET_CONNECT_TIMEOUT );
		socket.setReuseAddress(true);
		socket.setTcpNoDelay(true);

		// write and read headers
		SocketUtil.writeHeaders(socket.getOutputStream(), request);
		InputStream instream = socket.getInputStream();
		this.headers = SocketUtil.readHeaders(instream);

		// create contact
		host = socket.getInetAddress().getCanonicalHostName();
		final String user = SocketUtil.getHeaderValue(headers, "User");
		this.contact = new Contact(host, port, user);

		// handle request
		socket.setSoTimeout(Config.SOCKET_READ_TIMEOUT);
		if (!request.equals(RequestType.Status) && !request.equals(RequestType.Ping)) {
			Util.log(this, "Connected (Client).");
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {}
	}

	@Override
	public abstract String getId();

	public Contact getContact() {
		return contact;
	}

	public Socket getSocket() {
		return socket;
	}

	public RequestType getRequest() {
		return request;
	}

	public List<String> getHeaders() {
		return headers;
	}
}
