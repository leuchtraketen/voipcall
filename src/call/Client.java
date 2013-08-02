package call;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client implements Runnable, Activatable, Id, CloseListener, CloseListener.CloseListening {

	private boolean connected = false;
	private final String host;
	private final int port;
	private final String user;
	private Socket socket;

	private SocketUtil.RequestType request;
	private final List<String> headers;

	public CloseListeners closeListeners = new CloseListeners();

	public Client(String host, int port, SocketUtil.RequestType request) throws UnknownHostException,
			IOException {
		this.socket = new Socket(InetAddress.getByName(host), port);
		this.host = socket.getInetAddress().getCanonicalHostName();
		this.port = port;
		this.request = request;
		socket.setReuseAddress(true);

		SocketUtil.writeHeaders(socket.getOutputStream(), request);
		InputStream instream = socket.getInputStream();
		this.headers = SocketUtil.readHeaders(instream);
		this.user = SocketUtil.getHeaderValue(headers, "User");

		if (!request.equals(SocketUtil.RequestType.Status)) {
			socket.setSoTimeout(CallConfig.SOCKET_TIMEOUT);
			Util.log(this, "Connected (Client).");
		}
	}

	public static Client connect(String host, SocketUtil.RequestType request) throws UnknownHostException {
		Client client = null;
		int port = CallConfig.DEFAULT_PORT;
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
		connected = true;
		if (request.equals(SocketUtil.RequestType.Call)) {
			Peer peer = Calls.registerPeer(socket, headers);
			peer.closeListeners.add(this);
		}
	}

	@Override
	public boolean isActive() {
		return connected;
	}

	@Override
	public void close() {
		connected = false;
		try {
			socket.close();
		} catch (IOException e) {}
	}

	@Override
	public void onClose() {
		close();
		closeListeners.onClose();
	}

	@Override
	public String toString() {
		return user + "@" + host + ":" + port;
	}

	@Override
	public String getId() {
		return user + "@" + host + ":" + port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
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
		return getId().hashCode();
	}

	@Override
	public CloseListeners getCloseListeners() {
		return closeListeners;
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
