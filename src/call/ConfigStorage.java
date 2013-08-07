package call;

public interface ConfigStorage extends Id {

	boolean hasOption(Config.Option option);

	void setOption(Config.Option option, String value);

	String getOption(Config.Option option, String defaultValue);

	void addConfigListener(ConfigListener listener);

	void notifyConfigListener(ConfigListener listener);

}
