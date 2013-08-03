package call;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallFactory {
	
	

	private static final Map<Contact, Connection> connections = new HashMap<Contact, Connection>();

	public static CallThread createCall(Contact contact, Socket socket, List<String> headers) {
		CallThread call = new CallThread(contact, socket, headers);
		connections.put(contact, call);
		call.addCloseListener(new Cleanup(contact));
		CallUi.openCall(contact);
		for (Connection conn : CallUi.getUiListeners(contact)) {
			conn.addCloseListener(call);
			conn.addOpenListener(call);
			call.addCloseListener(conn);
			call.addOpenListener(conn);
		}
		return call;
	}

	public static Connection getConnection(Contact contact) {
		if (connections.containsKey(contact)) {
			Connection connection = connections.get(contact);
			if (connection.isConnected()) {
				return connection;
			} else {
				connections.remove(contact);
				return null;
			}
		} else {
			return null;
		}
	}

	private static class Cleanup extends AbstractConnection {

		private final Contact contact;

		public Cleanup(Contact contact) {
			this.contact = contact;
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public String getId() {
			return "Cleanup<" + contact.getId() + ">";
		}

		@Override
		public void close() {
			connections.remove(contact);
			super.close();
		}
	}

}
