package call;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AudioDevices<A extends AudioDevice> extends AbstractId {

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

	public abstract A getCurrentDevice() throws UnknownDefaultValueException;

	public abstract void setCurrentDevice(AudioDevice device);

}
