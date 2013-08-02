package call;

public class Contact implements Id {
	private final String host;
	private final int port;
	private final String user;

	public Contact(String host, int port, String user) {
		this.host = host;
		this.port = port;
		this.user = user;
	}

	public Contact(Client client) {
		this.host = client.getHost();
		this.port = client.getPort();
		this.user = client.getUser();
	}

	@Override
	public String toString() {
		if (port <= 0)
			return Util.firstToUpperCase(user) + "@" + host + " (incoming)";
		else if (port == CallConfig.DEFAULT_PORT)
			return Util.firstToUpperCase(user) + "@" + host;
		else
			return Util.firstToUpperCase(user) + "@" + host + ":" + port;
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
		if (obj != null && obj instanceof Contact) {
			Contact other = (Contact) obj;
			return other.host.equals(host) && other.user.equals(user) && other.port == port;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
