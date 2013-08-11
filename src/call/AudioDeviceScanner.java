package call;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
		Set<Microphone> microphones = discoverMicrophones();
		Set<Speaker> speakers = discoverSpeakers();
		int maxSteps = (microphones.size() + speakers.size()) * PcmFormatScanner.getMaxSteps();
		ui.setMaxSteps(maxSteps);
		microphones = discoverMicrophoneFormats(microphones);
		speakers = discoverSpeakerFormats(speakers);
		if (ui != null)
			ui.close();
		Microphones.setMicrophones(microphones);
		Speakers.setSpeakers(speakers);
	}

	private Set<Microphone> discoverMicrophoneFormats(Set<Microphone> microphones) {
		Microphones.Serializer serializer = new Microphones.Serializer(microphones);
		Map<Microphone, Collection<PcmFormat>> deserialized = new HashMap<>(
				Config.FORMATS_MICROPHONES.getDeserializedValue(serializer));

		for (Microphone microphone : microphones) {
			if (deserialized.containsKey(microphone)) {
				microphone.setFormats(deserialized.get(microphone));
			} else {
				Util.sleep(200);
				if (ui != null)
					ui.setCurrentLine(microphone.getMixerinfo(), microphone.getLineinfo());
				PcmFormatScanner scanner = new PcmFormatScanner(microphone.getLine(), ui);
				List<PcmFormat> formats = scanner.getFormats();
				microphone.setFormats(formats);
				deserialized.put(microphone, formats);
			}
		}

		Config.FORMATS_MICROPHONES.setDeserializedValue(deserialized, serializer);
		return microphones;
	}

	private Set<Speaker> discoverSpeakerFormats(Set<Speaker> speakers) {
		Speakers.Serializer serializer = new Speakers.Serializer(speakers);
		Map<Speaker, Collection<PcmFormat>> deserialized = new HashMap<>(
				Config.FORMATS_SPEAKERS.getDeserializedValue(serializer));

		for (Speaker speaker : speakers) {
			if (deserialized.containsKey(speaker)) {
				speaker.setFormats(deserialized.get(speaker));
			} else {
				Util.sleep(200);
				if (ui != null)
					ui.setCurrentLine(speaker.getMixerinfo(), speaker.getLineinfo());
				PcmFormatScanner scanner = new PcmFormatScanner(speaker.getLine(), ui);
				List<PcmFormat> formats = scanner.getFormats();
				speaker.setFormats(formats);
				deserialized.put(speaker, formats);
			}
		}

		Config.FORMATS_SPEAKERS.setDeserializedValue(deserialized, serializer);
		return speakers;
	}

	private Set<Microphone> discoverMicrophones() {
		Set<Microphone> microphones = new HashSet<>();
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
							microphones.add(new Microphone(new Microphone.Info(mixerinfo, mixer, lineinfo,
									(TargetDataLine) line)));
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
		return microphones;
	}

	private Set<Speaker> discoverSpeakers() {
		Set<Speaker> speakers = new HashSet<>();
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
							speakers.add(new Speaker(new Speaker.Info(mixerinfo, mixer, lineinfo,
									(SourceDataLine) line)));
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
		return speakers;
	}

	public void setUi(AudioDeviceScannerUi ui) {
		this.ui = ui;
	}

	@Override
	public String getId() {
		return "AudioDeviceScanner";
	}
}
