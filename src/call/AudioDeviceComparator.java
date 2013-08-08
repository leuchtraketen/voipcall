package call;

import java.util.Comparator;

public class AudioDeviceComparator implements Comparator<AudioDevice> {

	private static final String[] keywords = new String[] { "default" };

	@Override
	public int compare(AudioDevice device1, AudioDevice device2) {
		if (device1.equals(device2)) {
			return 0;
		}

		final String name1 = device1.getMixerinfo().getName().toLowerCase();
		final String name2 = device2.getMixerinfo().getName().toLowerCase();

		return compareByKeyword(name1, name2, keywords, 0);
	}

	private int compareByKeyword(String name1, String name2, String[] keywords, int i) {
		if (i < keywords.length) {
			if (name1.contains(keywords[i])) {
				return name2.contains(keywords[i]) ? compareByKeyword(name1, name2, keywords, i + 1) : -1;
			} else {
				return name2.contains(keywords[i]) ? 1 : compareByKeyword(name1, name2, keywords, i + 1);
			}
		} else {
			return name1.compareTo(name2);
		}
	}
}
