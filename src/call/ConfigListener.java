package call;

public interface ConfigListener extends Id {

	void onConfigUpdate(Config.Option option, float value);
	void onConfigUpdate(Config.Option option, int value);
	void onConfigUpdate(Config.Option option, boolean value);
	void onConfigUpdate(Config.Option option, String value);

}
