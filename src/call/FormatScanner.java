package call;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class FormatScanner {
	private final DataLine line;
	private final AudioDeviceScannerUi ui;

	public FormatScanner(DataLine line, AudioDeviceScannerUi ui) {
		this.line = line;
		this.ui = ui;
	}

	public List<Format> getFormats() {
		List<Format> formats = new ArrayList<>();

		for (float rate : Config.PCM_RATES) {
			for (int samplesize : Config.PCM_SAMPLE_SIZES) {
				for (int channels : Config.PCM_CHANNELS) {
					Format format = new PCMFormat(rate, samplesize, channels);
					boolean works = probFormat(format);
					if (works) {
						formats.add(format);
					}
					/*
					 * if (works) { System.out.println("works: " + format); }
					 * else { System.out.println("does NOT work: " + format); }
					 */
					if (ui != null)
						ui.nextProgressStep();
				}
			}
		}
		return formats;
	}

	private boolean probFormat(Format format) {
		try {
			if (line instanceof TargetDataLine) {
				((TargetDataLine) (line)).open(format.getAudioFormat(), line.getBufferSize());
				line.close();
			} else if (line instanceof SourceDataLine) {
				((TargetDataLine) (line)).open(format.getAudioFormat(), line.getBufferSize());
				line.close();
			} else {
				throw new RuntimeException("WTF???");
			}
			return true;
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		return false;
	}

	public static int getMaxSteps() {
		return Config.PCM_RATES.length * Config.PCM_SAMPLE_SIZES.length * Config.PCM_CHANNELS.length;
	}

}
