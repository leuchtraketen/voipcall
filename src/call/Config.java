package call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioFormat;

public class Config {

	public static final int UID = new Random(System.currentTimeMillis()).nextInt();
	public static final String UID_S = UID + "";

	public static final AudioFormat.Encoding ENCODING_PCM_SIGNED = AudioFormat.Encoding.PCM_SIGNED;
	public static final int[] PCM_RATES = calcPCMRates();
	public static final int PCM_DEFAULT_RATE = 44100;
	public static final boolean PCM_DEFAULT_BIG_ENDIAN = false;
	public static final int[] PCM_SAMPLE_SIZES = { 16, 8 };
	public static final int PCM_DEFAULT_SAMPLE_SIZE = 16;
	public static final int[] PCM_CHANNELS = { 1, 2 };
	public static final int PCM_DEFAULT_CHANNELS = 1;
	public static final PcmFormat PCM_DEFAULT_FORMAT = new PcmFormat(PCM_DEFAULT_RATE,
			PCM_DEFAULT_SAMPLE_SIZE, PCM_DEFAULT_CHANNELS);

	public static final int DEFAULT_PORT = 4000;
	public static final int DEFAULT_PORT_OFFSET_CALL = 1;
	public static final int DEFAULT_PORT_OFFSET_CHAT = 2;
	public static int CURRENT_PORT = DEFAULT_PORT;
	public static final int SOCKET_READ_TIMEOUT = 7000;
	public static final int SOCKET_CONNECT_TIMEOUT = 2000;
	public static final long CURRENT_UPTIME = System.currentTimeMillis();

	public static String[] DEFAULT_CONTACT_HOSTS = { "127.0.0.1", "192.168.223.3", "192.168.223.5",
			"192.168.223.7", "192.168.223.9", "192.168.223.150", "192.168.223.151", "192.168.223.152",
			"192.168.223.153", "192.168.223.154", "192.168.224.3", "192.168.224.5", "192.168.224.7",
			"192.168.224.9", "192.168.224.150", "192.168.224.151", "192.168.224.152", "192.168.224.153",
			"192.168.224.154", "192.168.25.100", "192.168.25.101", "192.168.25.102", "192.168.25.103",
			"dsl-ka.tobias-schulz.eu", "dsl-hg.tobias-schulz.eu", "freehal.net",

	};

	public static final BooleanOption SHOW_CONSOLE = new BooleanOption("show-console", true);
	public static final StringOption CUSTOM_CONTACTS = new StringOption("custom-contacts", "");
	public static final StringOption CONNECTED_CONTACTS = new StringOption("connected-contacts", "");
	public static final SerializedOption<Microphone> SELECTED_MICROPHONE = new SerializedOption<>(
			"selected-microphone", new Microphones.Serializer());
	public static final SerializedOption<Speaker> SELECTED_SPEAKER = new SerializedOption<>(
			"selected-speaker", new Speakers.Serializer());
	public static final IntegerOption BUFFER_SIZE_CALLS = new IntegerOption("buffer-size-calls", 16 * 1024);
	public static final IntegerOption SELECTED_PCM_RATE = new IntegerOption("pcm-rate",
			(int) PCM_DEFAULT_RATE);
	public static final IntegerOption SELECTED_PCM_SAMPLE_SIZE = new IntegerOption("pcm-sample-size",
			PCM_DEFAULT_SAMPLE_SIZE);
	public static final IntegerOption SELECTED_PCM_CHANNELS = new IntegerOption("pcm-channels",
			PCM_DEFAULT_CHANNELS);
	public static final MapOption<Microphone, Collection<PcmFormat>> FORMATS_MICROPHONES = new MapOption<>(
			"formats-microphones");
	public static final MapOption<Speaker, Collection<PcmFormat>> FORMATS_SPEAKERS = new MapOption<>(
			"formats-speakers");

	public static final Option ALL_OPTIONS[] = new Option[] { SHOW_CONSOLE, CUSTOM_CONTACTS,
			CONNECTED_CONTACTS, SELECTED_MICROPHONE, SELECTED_SPEAKER, BUFFER_SIZE_CALLS, SELECTED_PCM_RATE,
			SELECTED_PCM_SAMPLE_SIZE, SELECTED_PCM_CHANNELS, FORMATS_MICROPHONES, FORMATS_SPEAKERS };

	private static final ConfigStorage CONFIG_STORAGE = new DefaultConfigStorage();

	private static int[] calcPCMRates() {
		final int maxmult = 24;
		Integer[] rates = new Integer[2 * maxmult];

		int index = 0;
		for (int mult = 1; mult <= maxmult; ++mult) {
			for (int base : new int[] { 11025, 8000 }) {
				rates[index++] = base * mult;
			}
		}

		Arrays.sort(rates, Collections.reverseOrder());
		int[] ratesSorted = new int[2 * maxmult];
		for (int i = 0; i < rates.length; ++i) {
			ratesSorted[i] = rates[i];
		}

		return ratesSorted;

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

	public static class SerializedOption<A extends Id> extends AbstractOption {
		private static final String NULL = "null";
		private final String PREFIX;
		private final IdObjectSerializer<A> serializer;

		protected SerializedOption(String optionname, IdObjectSerializer<A> serializer) {
			super(optionname);
			this.serializer = serializer;
			this.PREFIX = "(" + serializer.getConfigPrefix() + ")";
		}

		public A getDeserializedValue() throws UnknownDefaultValueException {
			// try {
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(PREFIX) && !value.substring(PREFIX.length()).equals(NULL))
					return serializer.deserialize(value.substring(PREFIX.length()));
				else
					return serializer.getDefaultValue();
			} else {
				return serializer.getDefaultValue();
			}
			// } catch (UnknownDefaultValueException e) {
			// throw new UnsupportedOperationException(e.getMessage());
			// }
		}

		public void setDeserializedValue(A value) {
			setStringValue(PREFIX + serializer.serialize(value));
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getDefaultStringValue() {
			try {
				return PREFIX + serializer.getDefaultValue();
			} catch (UnknownDefaultValueException e) {
				return PREFIX + NULL;
			}
		}

		@Override
		public String getId() {
			return "SerializedOption<" + optionname + ">";
		}
	}

	public static class ListOption<A extends Id> extends AbstractOption {
		private static final String NULL = "null";
		private final String PREFIX;
		private final IdListSerializer<A> serializer;

		protected ListOption(String optionname, IdListSerializer<A> serializer) {
			super(optionname);
			this.serializer = serializer;
			this.PREFIX = "(" + serializer.getConfigPrefix() + ")";
		}

		public Collection<? extends A> getDeserializedValue() {
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(PREFIX) && !value.substring(PREFIX.length()).equals(NULL))
					return serializer.deserializeAll(value.substring(PREFIX.length()));
				else
					return new ArrayList<>();
			} else {
				return new ArrayList<>();
			}
		}

		public void setDeserializedValue(Collection<? extends A> value) {
			setStringValue(PREFIX + serializer.serializeAll(value));
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getId() {
			return "ListOption<" + optionname + ">";
		}

		@Override
		public String getDefaultStringValue() {
			return PREFIX + NULL;
		}
	}

	public static class MapOption<A extends Id, B> extends AbstractOption {
		private static final String NULL = "null";

		protected MapOption(String optionname) {
			super(optionname);
		}

		private String getPrefix(IdMapSerializer<A, B> serializer) {
			return "(" + serializer.getConfigPrefix() + ")";
		}

		public Map<? extends A, ? extends B> getDeserializedValue(IdMapSerializer<A, B> serializer) {
			final String prefix = getPrefix(serializer);
			if (CONFIG_STORAGE.hasOption(this)) {
				String value = CONFIG_STORAGE.getOption(this, "");
				if (value.startsWith(prefix) && !value.substring(prefix.length()).equals(NULL))
					return serializer.deserializeMap(value.substring(prefix.length()));
				else
					return new HashMap<>();
			} else {
				return new HashMap<>();
			}
		}

		public void setDeserializedValue(Map<? extends A, ? extends B> value, IdMapSerializer<A, B> serializer) {
			final String prefix = getPrefix(serializer);
			setStringValue(prefix + serializer.serializeMap(value));
		}

		@Override
		public String getStringValue() {
			return CONFIG_STORAGE.getOption(this, getDefaultStringValue());
		}

		@Override
		public String getDefaultStringValue() {
			return "(" + NULL + ")" + NULL;
		}

		@Override
		public String getId() {
			return "ListOption<" + optionname + ">";
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
