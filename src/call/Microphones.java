package call;

import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Microphones {

	private Set<Microphone> microphones = new HashSet<>();

	public Microphones() {
		microphones = discover();
	}

	private Set<Microphone> discover() {
		Set<Microphone> microphones = new HashSet<>();
		for (Mixer.Info mixerinfo : AudioSystem.getMixerInfo()) {
			System.out.println("mixerinfo: " + mixerinfo);
			Mixer mixer = AudioSystem.getMixer(mixerinfo);
			System.out.println("mixer:     " + mixer);
			System.out.println("mixerinfo: " + mixer.getLineInfo());
			for (Line.Info lineinfo : mixer.getTargetLineInfo()) {
				try {
					Line line;
					line = mixer.getLine(lineinfo);
					if (line instanceof TargetDataLine) {
						System.out.println("    lineinfo:   " + lineinfo);
						System.out.println("    line:       " + line);
						System.out.println("    lineinfo:   " + line.getLineInfo());
						if (mixer.isLineSupported(lineinfo)) {
							microphones
									.add(new Microphone(mixerinfo, mixer, lineinfo, (TargetDataLine) line));
						} else {
							System.out.println("    NOT SUPPORTED!");
						}
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		return microphones;
	}

	public Set<Microphone> getMicrophones() {
		return microphones;
	}
}
