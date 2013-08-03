package call;

import java.util.Random;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

public class Config {

	public static final int UID = new Random(System.currentTimeMillis()).nextInt();
	public static final String UID_S = UID + "";

	public static final AudioFormat.Encoding DEFAULT_ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public static final boolean DEFAULT_BIG_ENDIAN = false;
	public static final int DEFAULT_SAMPLE_SIZE = 16;
	public static final int DEFAULT_CHANNELS = 1;
	public static final float DEFAULT_RATE = 44100.0F;
	public static final int INTERNAL_BUFFER_SIZE = AudioSystem.NOT_SPECIFIED;
	public static final AudioFileFormat.Type DEFAULT_TARGET_TYPE = AudioFileFormat.Type.WAVE;

	public static final int DEFAULT_PORT = 4000;
	public static final int SOCKET_TIMEOUT = 7000;
	public static int CURRENT_PORT = DEFAULT_PORT;

	public static String[] DEFAULT_CONTACT_HOSTS = { "127.0.0.1", "192.168.223.3", "192.168.223.5",
			"192.168.223.7", "192.168.223.9", "192.168.223.150", "192.168.223.151", "192.168.223.152",
			"192.168.223.153", "192.168.223.154", "192.168.224.3", "192.168.224.5", "192.168.224.7",
			"192.168.224.9", "192.168.224.150", "192.168.224.151", "192.168.224.152", "192.168.224.153",
			"192.168.224.154", "192.168.25.100", "192.168.25.101", "192.168.25.102", "192.168.25.103",
			"dsl-ka.tobias-schulz.eu", "dsl-hg.tobias-schulz.eu", "freehal.net",

	};

}
