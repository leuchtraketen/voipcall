package call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
		return StringUtils.remove((deserialized.getMixerinfo().toString() + ", " + deserialized.getLineinfo()
				.toString()).toLowerCase().trim(), ";");
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

	@Override
	public String serializeAll(Collection<AudioDevice> deserialized) {
		List<String> serialized = new ArrayList<>();
		for (AudioDevice elem : deserialized) {
			serialized.add(serialize(elem));
		}
		return Util.join(serialized, ";");
	}

	@Override
	public Collection<AudioDevice> deserializeAll(String serialized) throws UnknownDefaultValueException {
		String[] serializedAll = StringUtils.split(serialized, ";");
		List<AudioDevice> deserialized = new ArrayList<>();
		for (String elem : serializedAll) {
			deserialized.add(deserialize(elem));
		}
		return deserialized;
	}

	public abstract A getCurrentDevice() throws UnknownDefaultValueException;

	public abstract void setCurrentDevice(AudioDevice device);

}
