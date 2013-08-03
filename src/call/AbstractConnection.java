package call;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConnection implements Connection {

	private static enum State {
		OPEN, CLOSED, VIRGIN
	};

	private State connected = State.VIRGIN;
	private boolean inOpen = false;
	private boolean inClose = false;

	private List<Connection> openListeners = new ArrayList<Connection>();
	private List<Connection> closeListeners = new ArrayList<Connection>();

	@Override
	public void addCloseListener(Connection connection) {
		System.out.println("close listener: " + this.getId() + " -> " + connection.getId());
		closeListeners.add(connection);
	}

	@Override
	public void addOpenListener(Connection connection) {
		System.out.println("open  listener: " + this.getId() + " -> " + connection.getId());
		openListeners.add(connection);
	}

	public void setConnected(boolean connected) {
		switch (this.connected) {
		case VIRGIN:
			if (connected)
				open();
			break;
		case CLOSED:
			if (connected)
				open();
			break;
		case OPEN:
			if (!connected)
				close();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isConnected() {
		return connected == State.OPEN ? true : false;
	}

	@Override
	public boolean isFinished() {
		return connected == State.CLOSED ? true : false;
	}

	@Override
	public void open() {
		if (!inOpen) {
			System.out.println("open():  " + this.getId());
			inOpen = true;
			connected = State.OPEN;
			for (Connection cl : openListeners) {
				System.out.println("- open():  " + cl.getId());
				cl.open();
			}
			inOpen = false;
		}
	}

	@Override
	public void close() {
		if (!inClose) {
			System.out.println("close(): " + this.getId());
			inClose = true;
			if (connected.equals(State.OPEN)) {
				connected = State.CLOSED;
				for (Connection cl : closeListeners) {
					System.out.println("- close(): " + cl.getId());
					cl.close();
				}
				closeListeners.clear();
				openListeners.clear();
			} else
				System.out.println("- (not open)");

			inClose = false;
		}
	}

	@Override
	public abstract String getId();

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
	public String toString() {
		return getId();
	}
}