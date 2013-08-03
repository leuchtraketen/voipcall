package call;

public class Contact implements Id {
	private final String host;
	private final int port;
	private final String user;
	private boolean reachable;

	public Contact(String host, int port, String user) {
		this.host = host.toLowerCase();
		this.port = port;
		this.user = user.toLowerCase();
		this.reachable = port > 0 ? true : false;
	}

	@Override
	public String toString() {
		if (!reachable)
			return Util.firstToUpperCase(user) + "@" + host + " (incoming)";
		else if (port == Config.DEFAULT_PORT)
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

	public boolean isReachable() {
		return reachable;
	}

	public boolean isHost(String host) {
		return this.host.equals(host.toLowerCase());
	}

	public boolean isPort(int port) {
		return this.port == port;
	}

	public boolean isUser(String user) {
		return this.user.equals(user.toLowerCase());
	}

	public void setReachable(boolean reachable) {
		this.reachable = reachable;
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
