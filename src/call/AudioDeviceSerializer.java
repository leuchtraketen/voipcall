package call;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public abstract class AudioDeviceSerializer<A extends AudioDevice> extends AbstractId implements
		IdObjectSerializer<A>, IdMapSerializer<A, Collection<PcmFormat>> {

	public abstract List<A> getAudioDevices();

	@Override
	public String serialize(A deserialized) {
		String serialized = (StringUtils.remove(deserialized.getMixerinfo().toString(), ",") + ", " + StringUtils
				.remove(deserialized.getLineinfo().toString(), ",")).toLowerCase().trim();
		serialized = StringUtils.remove(serialized, ";");
		serialized = StringUtils.remove(serialized, "|");
		serialized = StringUtils.remove(serialized, ":");
		return serialized;
	}

	@Override
	public A deserialize(String serialized) throws UnknownDefaultValueException {
		serialized = StringUtils.split(serialized, ":", 2)[0];
		Collection<A> devicelist = getAudioDevices();
		for (A device : devicelist) {
			final String deviceSerialized = StringUtils.split(serialize(device), ":", 2)[0];
			if (deviceSerialized.equals(serialized)) {
				return device;
			}
		}
		return this.getDefaultValue();
	}

	@Override
	public String serializeMap(Map<? extends A, ? extends Collection<PcmFormat>> value) {
		List<String> serialized = new ArrayList<>();
		for (A elem : value.keySet()) {
			String str = serialize(elem);
			final List<PcmFormat> formats = elem.getFormats();
			str += ":" + new PcmFormat.Serializer().serializeAll(formats);
			serialized.add(str);
		}
		return Util.join(serialized, "|");
	}

	@Override
	public Map<A, Collection<PcmFormat>> deserializeMap(String serialized) {
		String[] serializedAll = StringUtils.split(serialized, "|");
		Map<A, Collection<PcmFormat>> deserialized = new HashMap<>();
		for (String elem : serializedAll) {
			String[] parts = StringUtils.split(elem, ":", 2);
			if (parts.length == 2)
				try {
					deserialized.put(deserialize(parts[0]),
							new PcmFormat.Serializer().deserializeAll(parts[1]));
				} catch (UnknownDefaultValueException e) {
					e.printStackTrace();
				}
		}
		return deserialized;
	}

}
