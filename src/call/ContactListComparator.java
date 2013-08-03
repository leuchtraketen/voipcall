package call;

import java.util.Comparator;

public class ContactListComparator implements Comparator<Contact> {

	@Override
	public int compare(Contact c1, Contact c2) {
		final String cmp1 = compareStr(c1);
		final String cmp2 = compareStr(c2);
		return cmp1.compareTo(cmp2);
	}

	private String compareStr(Contact c) {
		boolean online = ContactList.isOnline(c);
		String str = "";
		str += online ? "1" : "2";
		str += c.isReachable() ? "1" : "2";
		str += c.getUser();
		str += c.getHost();
		str += c.getPort();
		return str;
	}
}
