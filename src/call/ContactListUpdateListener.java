package call;

public interface ContactListUpdateListener extends Id {
	void onContactUpdate(Contact contact);

	void onAnyContactUpdate();
}