package call;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

public class Config {

	public static final int UID = new Random(System.currentTimeMillis()).nextInt();
	public static final String UID_S = UID + "";

	public static final AudioFormat.Encoding ENCODING_PCM_SIGNED = AudioFormat.Encoding.PCM_SIGNED;
	public static final Float[] PCM_RATES = calcPCMRates();
	public static final float PCM_DEFAULT_RATE = 44100.0F;
	public static final boolean PCM_DEFAULT_BIG_ENDIAN = false;
	public static final int[] PCM_SAMPLE_SIZES = { 16, 8 };
	public static final int PCM_DEFAULT_SAMPLE_SIZE = 16;
	public static final int[] PCM_CHANNELS = { 1, 2 };
	public static final int PCM_DEFAULT_CHANNELS = 1;

	public static final AudioFormat.Encoding DEFAULT_ENCODING = ENCODING_PCM_SIGNED;
	public static final int INTERNAL_BUFFER_SIZE = AudioSystem.NOT_SPECIFIED;
	public static final AudioFileFormat.Type DEFAULT_TARGET_TYPE = AudioFileFormat.Type.WAVE;

	public static final int DEFAULT_PORT = 4000;
	public static final int SOCKET_TIMEOUT = 7000;
	public static final long CURRENT_UPTIME = System.currentTimeMillis();
	public static int CURRENT_PORT = DEFAULT_PORT;

	public static String[] DEFAULT_CONTACT_HOSTS = { "127.0.0.1", "192.168.223.3", "192.168.223.5",
			"192.168.223.7", "192.168.223.9", "192.168.223.150", "192.168.223.151", "192.168.223.152",
			"192.168.223.153", "192.168.223.154", "192.168.224.3", "192.168.224.5", "192.168.224.7",
			"192.168.224.9", "192.168.224.150", "192.168.224.151", "192.168.224.152", "192.168.224.153",
			"192.168.224.154", "192.168.25.100", "192.168.25.101", "192.168.25.102", "192.168.25.103",
			"dsl-ka.tobias-schulz.eu", "dsl-hg.tobias-schulz.eu", "freehal.net",

	};

	public static final BooleanOption SHOW_CONSOLE = new BooleanOption("show-console", true);
	public static final StringOption CUSTOM_CONTACTS = new StringOption("custom-contacts", "");

	public static final Option ALL_OPTIONS[] = new Option[] { SHOW_CONSOLE, CUSTOM_CONTACTS };

	private static final ConfigStorage CONFIG_STORAGE = new DefaultConfigStorage();

	private static Float[] calcPCMRates() {
		final int maxmult = 24;
		Float[] rates = new Float[2 * maxmult];

		int index = 0;
		for (int mult = 1; mult <= maxmult; ++mult) {
			for (float base : new float[] { 11025.0f, 8000.0f }) {
				rates[index++] = base * mult;
			}
		}

		Arrays.sort(rates, Collections.reverseOrder());

		return rates;

		// { 8 * 22050.0f, 7 * 22050.0f, 6 * 22050.0f, 5 * 22050.0f,
		// 4 * 22050.0f, 3 * 22050.0f, 2 * 22050.0f, 22050.0f, 16000.0f,
		// 11025.0f, 8000.0f }
	}

	public static interface Option extends Id {

		String getName();

		String getDefaultStringValue();

		String getStringValue();

		void setStringValue(String value);

	}

	public static abstract class AbstractOption extends AbstractId implements Option {
		protected final String optionname;

		protected AbstractOption(final String optionname) {
			this.optionname = optionname;
		}

		public String toString() {
			return optionname;
		}

		@Override
		public String getName() {
			return optionname;
		}

		@Override
		public abstract String getDefaultStringValue();

		@Override
		public void setStringValue(String value) {
			CONFIG_STORAGE.setOption(this, value);
		}

		@Override
		public abstract String getStringValue();
	}

	public static class StringOption extends AbstractOption {
		protected final String defaultvalue;

		protected StringOption(String optionname, String defaultvalue) {
			super(optionname);
			this.defaultvalue = defaultvalue;
		}

		@Override
		public String getId() {
			return "StringOption<" + optionname + ">";
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, "" + defaultvalue);
		}

		@Override
		public String getDefaultStringValue() {
			return defaultvalue;
		}
	}

	public static class BooleanOption extends AbstractOption {
		private static final String PREFIX = "(boolean)";
		private boolean defaultvalue;

		protected BooleanOption(String optionname, boolean defaultvalue) {
			super(optionname);
			this.defaultvalue = defaultvalue;
		}

		public boolean getBooleanValue() {
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(PREFIX) && Primitives.isBoolean(value.substring(PREFIX.length())))
					return Primitives.toBoolean(value.substring(PREFIX.length()), defaultvalue);
				else
					return defaultvalue;
			} else {
				return defaultvalue;
			}
		}

		public void setBooleanValue(boolean value) {
			setStringValue(PREFIX + value);
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getDefaultStringValue() {
			return PREFIX + defaultvalue;
		}

		@Override
		public String getId() {
			return "BooleanOption<" + optionname + ">";
		}
	}

	public static class IntegerOption extends AbstractOption {
		private static final String PREFIX = "(int)";
		private int defaultvalue;

		protected IntegerOption(String optionname, int defaultvalue) {
			super(optionname);
			this.defaultvalue = defaultvalue;
		}

		public int getIntegerValue() {
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(PREFIX) && Primitives.isBoolean(value.substring(PREFIX.length())))
					return Primitives.toInteger(value.substring(PREFIX.length()), defaultvalue);
				else
					return defaultvalue;
			} else {
				return defaultvalue;
			}
		}

		public void setIntegerValue(int value) {
			setStringValue(PREFIX + value);
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getDefaultStringValue() {
			return PREFIX + defaultvalue;
		}

		@Override
		public String getId() {
			return "IntegerOption<" + optionname + ">";
		}
	}

	public static class FloatOption extends AbstractOption {
		private static final String PREFIX = "(float)";
		private float defaultvalue;

		protected FloatOption(String optionname, float defaultvalue) {
			super(optionname);
			this.defaultvalue = defaultvalue;
		}

		public float getFloatValue() {
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(PREFIX) && Primitives.isBoolean(value.substring(PREFIX.length())))
					return Primitives.toFloat(value.substring(PREFIX.length()), defaultvalue);
				else
					return defaultvalue;
			} else {
				return defaultvalue;
			}
		}

		public void setFloatValue(float value) {
			setStringValue(PREFIX + value);
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getDefaultStringValue() {
			return PREFIX + defaultvalue;
		}

		@Override
		public String getId() {
			return "FloatOption<" + optionname + ">";
		}
	}

	public static Option fromString(String optionname) {
		if (optionname != null) {
			for (Option option : ALL_OPTIONS) {
				if (optionname.equalsIgnoreCase(option.getName())) {
					return option;
				}
			}
		}
		return null;
	}

	public static Map<Option, String> getDefaultValues() {
		Map<Option, String> defaultValues = new HashMap<>();
		for (Option option : ALL_OPTIONS) {
			defaultValues.put(option, option.getDefaultStringValue());
		}
		return defaultValues;
	}

	public static void addConfigListener(ConfigListener listener) {
		CONFIG_STORAGE.addConfigListener(listener);
	}

	public static void notifyConfigListener(ConfigListener listener) {
		CONFIG_STORAGE.notifyConfigListener(listener);
	}
}
