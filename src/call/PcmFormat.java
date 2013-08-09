package call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.apache.commons.lang3.StringUtils;

public class PcmFormat extends AbstractId implements Format {

	private final int rate;
	private final int samplesize;
	private final int channels;
	private final AudioFormat audioformat;

	public PcmFormat(int rate, int samplesize, int channels) {
		this.rate = rate;
		this.samplesize = samplesize;
		this.channels = channels;
		this.audioformat = new AudioFormat(Config.ENCODING_PCM_SIGNED, rate, samplesize, channels,
				(samplesize / 8) * channels, rate, Config.PCM_DEFAULT_BIG_ENDIAN);
	}

	public int getRate() {
		return rate;
	}

	public int getSamplesize() {
		return samplesize;
	}

	public int getChannels() {
		return channels;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioformat;
	}

	@Override
	public String getId() {
		return "rate=" + rate + ",samplesize=" + samplesize + ",channels=" + channels;
	}

	public float getBitrate() {
		return calcBitrate(rate, samplesize, channels);
	}

	public float getByterate() {
		return getBitrate() / 8;
	}

	@Override
	public String toString() {
		return "PCM: rate=" + rate + ", samplesize=" + samplesize + ", channels=" + channels + ", bitrate="
				+ getBitrate() + ", kB/s=" + (getByterate() / 1024) + ")";
	}

	public static float calcBitrate(float rate, int samplesize, int channels) {
		return rate * samplesize * channels;
	}

	public static class Serializer implements IdSerializer<PcmFormat> {

		@Override
		public String serialize(PcmFormat deserialized) {
			return StringUtils.join(
					Arrays.asList(new String[] { "" + deserialized.getRate(),
							"" + deserialized.getSamplesize(), "" + deserialized.getChannels() }), ",");
		}

		@Override
		public PcmFormat deserialize(String serialized) throws UnknownDefaultValueException {
			String[] parts = StringUtils.split(serialized, ",");
			if (parts.length == 3 && Primitives.isInteger(parts[0]) && Primitives.isInteger(parts[1])
					&& Primitives.isInteger(parts[2]))
				return new PcmFormat(Primitives.toInteger(parts[0], 0), Primitives.toInteger(parts[1], 0),
						Primitives.toInteger(parts[2], 0));
			else
				return getDefaultValue();
		}

		@Override
		public String serializeAll(Collection<PcmFormat> deserialized) {
			List<String> serialized = new ArrayList<>();
			for (PcmFormat format : deserialized) {
				serialized.add(serialize(format));
			}
			return StringUtils.join(serialized, ";");
		}

		@Override
		public Collection<PcmFormat> deserializeAll(String serialized) throws UnknownDefaultValueException {
			List<PcmFormat> deserialized = new ArrayList<>();
			String[] all = StringUtils.split(serialized, ";");
			for (String elem : all) {
				deserialized.add(deserialize(elem));
			}
			return deserialized;
		}

		@Override
		public String getConfigPrefix() {
			return "(pcmformat)";
		}

		@Override
		public PcmFormat getDefaultValue() throws UnknownDefaultValueException {
			throw new UnknownDefaultValueException("???");
		}

	}
}
