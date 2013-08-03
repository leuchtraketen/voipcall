package call;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Call extends AbstractId {

	private final Contact contact;
	private final Socket socket;
	private final List<String> headers;
	private final Set<Connection> connections = new HashSet<Connection>();

	public Call(Contact contact, Socket socket, List<String> headers) {
		this.contact = contact;
		this.socket = socket;
		this.headers = headers;
	}

	public void addConnection(Connection connection) {
		connections.add(connection);
	}

	public Contact getContact() {
		return contact;
	}

	public void close() {
		for (Connection connection : connections) {
			connection.close();
		}
		try {
			socket.close();
		} catch (IOException e) {}
	}

	public void open() {
		for (Connection connection : connections) {
			connection.open();
		}
	}

	@Override
	public String getId() {
		return "Call<" + contact.getId() + ">";
	}

}
