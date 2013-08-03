package call;

public interface Connection extends Id {

	boolean isConnected();

	void open();

	void close();

}
