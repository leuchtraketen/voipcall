package call;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioDeviceScanner extends AbstractId implements Runnable {

	private AudioDeviceScannerUi ui;

	public AudioDeviceScanner() {}

	private static final boolean DEBUG = false;

	@Override
	public void run() {
		if (ui != null)
			ui.open();
		Set<Microphone.Info> microphoneinfos = discoverMicrophoneInfos();
		Set<Speaker.Info> speakerinfos = discoverSpeakerInfos();
		int maxSteps = (microphoneinfos.size() + speakerinfos.size()) * PcmFormatScanner.getMaxSteps();
		ui.setMaxSteps(maxSteps);
		Set<Microphone> microphones = discoverMicrophoneFormats(microphoneinfos);
		Set<Speaker> speakers = discoverSpeakerFormats(speakerinfos);
		if (ui != null)
			ui.close();
		Microphones.setMicrophones(microphones);
		Speakers.setSpeakers(speakers);
	}

	private Set<Microphone> discoverMicrophoneFormats(Set<Microphone.Info> microphoneinfos) {
		Set<Microphone> microphones = new HashSet<>();
		for (Microphone.Info info : microphoneinfos) {
			Util.sleep(200);
			if (ui != null)
				ui.setCurrentLine(info.getMixerinfo(), info.getLineinfo());
			PcmFormatScanner scanner = new PcmFormatScanner(info.getLine(), ui);
			List<PcmFormat> formats = scanner.getFormats();
			microphones.add(new Microphone(info, formats));
		}
		return microphones;
	}

	private Set<Speaker> discoverSpeakerFormats(Set<Speaker.Info> speakerinfos) {
		Set<Speaker> speakers = new HashSet<>();
		for (Speaker.Info info : speakerinfos) {
			Util.sleep(200);
			if (ui != null)
				ui.setCurrentLine(info.getMixerinfo(), info.getLineinfo());
			PcmFormatScanner scanner = new PcmFormatScanner(info.getLine(), ui);
			List<PcmFormat> formats = scanner.getFormats();
			speakers.add(new Speaker(info, formats));
		}
		return speakers;
	}

	private Set<Microphone.Info> discoverMicrophoneInfos() {
		Set<Microphone.Info> microphoneinfos = new HashSet<>();
		for (Mixer.Info mixerinfo : AudioSystem.getMixerInfo()) {
			if (DEBUG)
				System.out.println("mixerinfo: " + mixerinfo);
			Mixer mixer = AudioSystem.getMixer(mixerinfo);
			if (DEBUG)
				System.out.println("mixer:     " + mixer);
			if (DEBUG)
				System.out.println("mixerinfo: " + mixer.getLineInfo());
			for (Line.Info lineinfo : mixer.getTargetLineInfo()) {
				try {
					Line line;
					line = mixer.getLine(lineinfo);
					if (line instanceof TargetDataLine) {
						if (DEBUG)
							System.out.println("    lineinfo:   " + lineinfo);
						if (DEBUG)
							System.out.println("    line:       " + line);
						if (DEBUG)
							System.out.println("    lineinfo:   " + line.getLineInfo());
						if (mixer.isLineSupported(lineinfo)) {
							microphoneinfos.add(new Microphone.Info(mixerinfo, mixer, lineinfo,
									(TargetDataLine) line));
						} else {
							if (DEBUG)
								System.out.println("    NOT SUPPORTED!");
						}
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		return microphoneinfos;
	}

	private Set<Speaker.Info> discoverSpeakerInfos() {
		Set<Speaker.Info> speakerinfos = new HashSet<>();
		for (Mixer.Info mixerinfo : AudioSystem.getMixerInfo()) {
			if (DEBUG)
				System.out.println("mixerinfo: " + mixerinfo);
			Mixer mixer = AudioSystem.getMixer(mixerinfo);
			if (DEBUG)
				System.out.println("mixer:     " + mixer);
			if (DEBUG)
				System.out.println("mixerinfo: " + mixer.getLineInfo());
			for (Line.Info lineinfo : mixer.getSourceLineInfo()) {
				try {
					Line line;
					line = mixer.getLine(lineinfo);
					if (line instanceof SourceDataLine) {
						if (DEBUG)
							System.out.println("    lineinfo:   " + lineinfo);
						if (DEBUG)
							System.out.println("    line:       " + line);
						if (DEBUG)
							System.out.println("    lineinfo:   " + line.getLineInfo());
						if (mixer.isLineSupported(lineinfo)) {
							speakerinfos.add(new Speaker.Info(mixerinfo, mixer, lineinfo,
									(SourceDataLine) line));
						} else {
							if (DEBUG)
								System.out.println("    NOT SUPPORTED!");
						}
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		return speakerinfos;
	}

	public void setUi(AudioDeviceScannerUi ui) {
		this.ui = ui;
	}

	@Override
	public String getId() {
		return "AudioDeviceScanner";
	}
}
