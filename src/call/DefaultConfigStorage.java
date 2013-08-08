package call;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import call.Config.BooleanOption;
import call.Config.FloatOption;
import call.Config.IntegerOption;
import call.Config.Option;
import call.Config.SerializedOption;

public class DefaultConfigStorage extends AbstractId implements ConfigStorage {

	private File configfile;
	private Map<Option, String> options;
	private Set<ConfigListener> listeners = new HashSet<>();

	public DefaultConfigStorage() {
		configfile = findConfigFile();
		load();
		save();
	}

	private File findConfigFile() {
		File directory = Util.isWindows() ? new File(System.getenv("APPDATA")) : new File(
				System.getProperty("user.home"), ".config");
		directory.mkdirs();
		directory = new File(directory, "calls");
		directory.mkdirs();

		return new File(directory, Util.isWindows() ? "config.ini" : "callrc");
	}

	private synchronized void load() {
		options = Config.getDefaultValues();
		try {
			BufferedReader in = new BufferedReader(new FileReader(configfile));

			String line;
			while ((line = in.readLine()) != null) {
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {
					Option option = Config.fromString(parts[0].trim());
					String value = parts[1].trim();
					if (option != null) {
						options.put(option, value);
					}
				}
			}
			in.close();

		} catch (FileNotFoundException e) {} catch (IOException e) {
			e.printStackTrace();
		}
		notifyConfigListeners();
	}

	private synchronized void notifyConfigListeners() {
		for (ConfigListener listener : listeners) {
			notifyConfigListener(listener);
		}
	}

	private synchronized void notifyConfigListeners(Option option) {
		for (ConfigListener listener : listeners) {
			notifyConfigListener(listener, option);
		}
	}

	private void notifyConfigListener(ConfigListener listener, Option option) {
		if (option instanceof FloatOption) {
			// Util.log(this, "notify1: " + listener);
			listener.onConfigUpdate(option, ((FloatOption) option).getFloatValue());
		} else if (option instanceof BooleanOption) {
			// Util.log(this, "notify2: " + listener);
			listener.onConfigUpdate(option, ((BooleanOption) option).getBooleanValue());
		} else if (option instanceof IntegerOption) {
			// Util.log(this, "notify3: " + listener);
			listener.onConfigUpdate(option, ((IntegerOption) option).getIntegerValue());
		} else if (option instanceof SerializedOption) {
			// Util.log(this, "notify4: " + listener);
			try {
				listener.onConfigUpdate(option, ((SerializedOption<?>) option).getDeserializedValue());
			} catch (UnknownDefaultValueException e) {}
		} else {
			// Util.log(this, "notify5: " + listener);
			listener.onConfigUpdate(option, option.getStringValue());
		}
	}

	public void notifyConfigListener(ConfigListener listener) {
		for (Option option : options.keySet()) {
			notifyConfigListener(listener, option);
		}
	}

	private synchronized void save() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(configfile));

			List<Option> optionsSorted = new ArrayList<>(options.keySet());
			Collections.sort(optionsSorted);
			for (Option option : optionsSorted) {
				out.println(option + " = " + options.get(option));
			}
			out.close();

		} catch (FileNotFoundException e) {} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		load();
	}

	public void reset() {
		options.clear();
		save();
		load();
	}

	@Override
	public void addConfigListener(ConfigListener listener) {
		listeners.add(listener);
	}

	@Override
	public String getId() {
		return "DefaultConfigStorage<" + configfile + ">";
	}

	@Override
	public boolean hasOption(Option option) {
		return options.containsKey(option);
	}

	@Override
	public void setOption(Option option, String value) {
		options.put(option, value);
		Util.log(option.toString(), "set => " + value);
		save();
		notifyConfigListeners(option);
	}

	@Override
	public String getOption(Option option, String defaultValue) {
		return options.containsKey(option) ? options.get(option) : defaultValue;
	}

}
