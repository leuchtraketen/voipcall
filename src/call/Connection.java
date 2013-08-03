package call;

public interface Connection extends Id {

	boolean isConnected();

	void open();

	void close();

	void addOpenListener(Connection connection);

	void addCloseListener(Connection connection);

	boolean isFinished();

}
