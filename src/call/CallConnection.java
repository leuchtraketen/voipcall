package call;

public interface CallConnection extends Id {

	boolean isCallOpen();

	void onCallOpen();

	void onCallClose();

}
