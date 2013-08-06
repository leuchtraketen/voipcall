package call;

import javax.sound.sampled.AudioFormat;

public class PCMFormat extends AbstractId implements Format {

	private final float rate;
	private final int samplesize;
	private final int channels;
	private final AudioFormat audioformat;

	public PCMFormat(float rate, int samplesize, int channels) {
		this.rate = rate;
		this.samplesize = samplesize;
		this.channels = channels;
		this.audioformat = new AudioFormat(Config.ENCODING_PCM_SIGNED, rate, samplesize, channels,
				(samplesize / 8) * channels, rate, Config.PCM_DEFAULT_BIG_ENDIAN);
	}

	public float getRate() {
		return rate;
	}

	public float getSamplesize() {
		return samplesize;
	}

	public float getChannels() {
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

}
