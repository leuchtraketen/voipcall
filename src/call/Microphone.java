package call;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Microphone {

	private final Mixer.Info mixerinfo;
	private final Mixer mixer;
	private final Line.Info lineinfo;
	private final TargetDataLine line;
	private final List<Format> formats;

	public Microphone(Mixer.Info mixerinfo, Mixer mixer, Line.Info lineinfo, TargetDataLine line) {
		this.mixerinfo = mixerinfo;
		this.mixer = mixer;
		this.lineinfo = lineinfo;
		this.line = line;
		this.formats = findFormats();
	}

	private List<Format> findFormats() {
		List<Format> formats = new ArrayList<>();

		for (float rate : Config.PCM_RATES) {
			for (int samplesize : Config.PCM_SAMPLE_SIZES) {
				for (int channels : Config.PCM_CHANNELS) {
					Format format = new PCMFormat(rate, samplesize, channels);
					boolean works = probFormat(format);
					if (works) {
						System.out.println("works: " + format);
					} else {
						System.out.println("does NOT work: " + format);
					}
				}
			}
		}
		return formats;
	}

	private boolean probFormat(Format format) {
		try {
			line.open(format.getAudioFormat(), line.getBufferSize());
			line.close();
			return true;
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		return false;
	}

	public Mixer.Info getMixerinfo() {
		return mixerinfo;
	}

	public Mixer getMixer() {
		return mixer;
	}

	public Line.Info getLineinfo() {
		return lineinfo;
	}

	public Line getLine() {
		return line;
	}

	public List<Format> getFormats() {
		return formats;
	}

}
