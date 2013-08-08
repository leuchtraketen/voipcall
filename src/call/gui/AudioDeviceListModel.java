package call.gui;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import call.AudioDevice;
import call.AudioDeviceUpdateListener;
import call.AudioDevices;
import call.Id;
import call.UnknownDefaultValueException;

public class AudioDeviceListModel extends AbstractListModel<AudioDevice> implements
		ComboBoxModel<AudioDevice>, AudioDeviceUpdateListener {

	private static final long serialVersionUID = -8791689168786668188L;

	private final AudioDevices<? extends AudioDevice> devices;

	public AudioDeviceListModel(AudioDevices<? extends AudioDevice> devices) {
		this.devices = devices;
		devices.addListener(this);
	}

	@Override
	public AudioDevice getElementAt(int index) {
		List<? extends AudioDevice> list = devices.getAudioDevices();
		if (index < list.size())
			return list.get(index);
		else if (list.size() > 0)
			return list.get(list.size() - 1);
		else
			return null;
	}

	@Override
	public int getSize() {
		return devices.getAudioDevices().size();
	}

	@Override
	public Object getSelectedItem() {
		try {
			return devices.getCurrentDevice();
		} catch (UnknownDefaultValueException e) {
			return null;
		}
	}

	@Override
	public void setSelectedItem(Object device) {
		devices.setCurrentDevice((AudioDevice) device);
	}

	@Override
	public void onAudioDeviceUpdate() {
		this.fireContentsChanged(this, 0, getSize());
	}

	@Override
	public String getId() {
		return "AudioDeviceListModel<" + devices.getId() + ">";
	}

	@Override
	public int compareTo(Id o) {
		return getId().compareTo(o.getId());
	}
}
