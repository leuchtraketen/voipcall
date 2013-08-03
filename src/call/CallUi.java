package call;

import java.util.List;

public class CallUi {
	private static CallUiAdapter instance = null;

	public static void register(CallUiAdapter instance) {
		CallUi.instance = instance;
	}

	public static void openCall(Contact contact) {
		instance.openCall(contact);
	}

	public static List<Connection> getUiListeners(Contact contact) {
		return instance.getUiListeners(contact);
	}

	public static interface CallUiAdapter {

		public abstract void openCall(Contact contact);

		public abstract List<Connection> getUiListeners(Contact contact);

	}

}
