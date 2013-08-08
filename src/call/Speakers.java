package call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Speakers extends AudioDevices<Speaker> {

	private static final Speakers instance = new Speakers();
	private static List<Speaker> speakers = new ArrayList<>();

	private Speakers() {}

	public static Speakers getInstance() {
		return instance;
	}

	public static List<Speaker> getSpeakers() {
		return speakers;
	}

	public static void setSpeakers(Collection<Speaker> speakers) {
		Speakers.speakers = new ArrayList<>(speakers);
		Collections.sort(Speakers.speakers, new AudioDeviceComparator());
	}

	@Override
	public String getId() {
		return "Speakers";
	}

	@Override
	public List<Speaker> getAudioDevices() {
		return speakers;
	}

	public static void setCurrentSpeaker(Speaker selected) {
		instance.setCurrentDevice(selected);
	}

	public static Speaker getCurrentSpeaker() throws UnknownDefaultValueException {
		return instance.getCurrentDevice();
	}

	@Override
	public Speaker getCurrentDevice() throws UnknownDefaultValueException {
		return (Speaker) Config.SELECTED_SPEAKER.getDeserializedValue();
	}

	@Override
	public void setCurrentDevice(AudioDevice selected) {
		AudioDevice current = null;
		try {
			current = Config.SELECTED_SPEAKER.getDeserializedValue();
		} catch (UnknownDefaultValueException e) {}

		if (current == null || !current.equals(selected)) {
			Config.SELECTED_SPEAKER.setDeserializedValue(selected);
		}
	}

	@Override
	public String getConfigPrefix() {
		return "speaker";
	}

	@Override
	public AudioDevice getDefaultValue() throws NoAudioDeviceException {
		List<Speaker> devicelist = getAudioDevices();
		if (devicelist.size() > 0) {
			return devicelist.get(0);
		} else {
			throw new NoAudioDeviceException("No speaker found!");
		}
	}
}
