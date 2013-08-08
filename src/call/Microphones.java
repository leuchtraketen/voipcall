package call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Microphones extends AudioDevices<Microphone> {

	private static final Microphones instance = new Microphones();
	private static List<Microphone> microphones = new ArrayList<>();

	private Microphones() {}

	public static Microphones getInstance() {
		return instance;
	}

	public static List<Microphone> getMicrophones() {
		return microphones;
	}

	public static void setMicrophones(Collection<Microphone> microphones) {
		Microphones.microphones = new ArrayList<>(microphones);
		Collections.sort(Microphones.microphones, new AudioDeviceComparator());
	}

	@Override
	public String getId() {
		return "Microphones";
	}

	@Override
	public List<Microphone> getAudioDevices() {
		return microphones;
	}

	public static void setCurrentMicrophone(Microphone selected) {
		instance.setCurrentDevice(selected);
	}

	public static Microphone getCurrentMicrophone() throws UnknownDefaultValueException {
		return instance.getCurrentDevice();
	}

	@Override
	public Microphone getCurrentDevice() throws UnknownDefaultValueException {
		return (Microphone) Config.SELECTED_MICROPHONE.getDeserializedValue();
	}

	@Override
	public void setCurrentDevice(AudioDevice selected) {
		AudioDevice current = null;
		try {
			current = Config.SELECTED_MICROPHONE.getDeserializedValue();
		} catch (UnknownDefaultValueException e) {}
		
		if (current == null || !current.equals(selected)) {
			Config.SELECTED_MICROPHONE.setDeserializedValue(selected);
		}
	}

	@Override
	public String getConfigPrefix() {
		return "microphone";
	}

	@Override
	public AudioDevice getDefaultValue() throws NoAudioDeviceException {
		List<Microphone> devicelist = getAudioDevices();
		if (devicelist.size() > 0) {
			return devicelist.get(0);
		} else {
			throw new NoAudioDeviceException("No microphone found!");
		}
	}
}
