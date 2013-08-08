package call;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AudioDevices<A extends AudioDevice> extends AbstractId implements
		IdSerializer<AudioDevice> {

	Set<AudioDeviceUpdateListener> listeners = new HashSet<>();

	public abstract List<A> getAudioDevices();

	public void addListener(AudioDeviceUpdateListener listener) {
		listeners.add(listener);
	}

	protected void notifyListeners() {
		for (AudioDeviceUpdateListener listener : listeners) {
			listener.onAudioDeviceUpdate();
		}
	}

	@Override
	public String serialize(AudioDevice deserialized) {
		return (deserialized.getMixerinfo().toString() + ", " + deserialized.getLineinfo().toString())
				.toLowerCase().trim();
	}

	@Override
	public AudioDevice deserialize(String serialized) throws UnknownDefaultValueException {
		List<A> devicelist = getAudioDevices();
		for (A device : devicelist) {
			if (serialize(device).equals(serialized)) {
				return device;
			}
		}
		return this.getDefaultValue();
	}

	public abstract A getCurrentDevice() throws UnknownDefaultValueException;

	public abstract void setCurrentDevice(AudioDevice device);

}
