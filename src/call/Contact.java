package call;

public class Contact extends AbstractId {
	
	public static enum Reachability {
		NORMAL, UNREACHABLE, LOOPBACK
	}

	private final String host;
	private final int port;
	private final String user;
	private final Reachability reachability;

	public Contact(String host, int port, String user) {
		this.host = host.toLowerCase();
		this.port = port;
		this.user = user.toLowerCase();
		this.reachability = Reachability.NORMAL;
	}

	public Contact(String host, int port, String user, Reachability reachability) {
		this.host = host.toLowerCase();
		this.port = port;
		this.user = user.toLowerCase();
		this.reachability = reachability;
	}

	@Override
	public String toString() {
		// Util.firstToUpperCase(user)
		switch (reachability) {
		case LOOPBACK:
			return user + "@" + host + " (loop)";
		case UNREACHABLE:
			return user + "@" + host + " (unreachable)";
		case NORMAL:
		default:
			if (port == Config.DEFAULT_PORT)
				return user + "@" + host;
			else
				return user + "@" + host + ":" + port;
		}
	}

	@Override
	public String getId() {
		switch (reachability) {
		case LOOPBACK:
			return user + "@" + host + ":" + port + " (loop)";
		case UNREACHABLE:
		case NORMAL:
		default:
			return user + "@" + host + ":" + port;
		}
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
		return !isUnreachable();
	}

	public boolean isUnreachable() {
		return reachability.equals(Reachability.UNREACHABLE) || reachability.equals(Reachability.LOOPBACK);
	}

	public boolean isLoop() {
		return reachability.equals(Reachability.LOOPBACK);
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
}
