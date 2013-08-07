package call;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Call extends AbstractId {

	private final Contact contact;
	private final Socket socket;
	@SuppressWarnings("unused")
	private final List<String> headers;
	private final Set<CallConnection> connections = new HashSet<>();
	private ConnectionState state;

	public Call(Contact contact, Socket socket, List<String> headers) {
		this.contact = contact;
		this.socket = socket;
		this.headers = headers;
		this.state = ConnectionState.CONNECTING;
	}

	public void addConnection(CallConnection connection) {
		connections.add(connection);
	}

	public Contact getContact() {
		return contact;
	}

	public synchronized void close() {
		if (!state.equals(ConnectionState.CLOSED)) {
			state = ConnectionState.CLOSED;
			for (CallConnection connection : new HashSet<>(connections)) {
				connection.onCallClose();
			}
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}

	public synchronized void open() {
		if (!state.equals(ConnectionState.OPEN)) {
			state = ConnectionState.OPEN;
			for (CallConnection connection : new HashSet<>(connections)) {
				connection.onCallOpen();
			}
		}
	}

	public ConnectionState getState() {
		return state;
	}

	@Override
	public String getId() {
		return "Call<" + contact.getId() + ">";
	}

}
