package call;

public class CallUi {
	private static CallUiAdapter instance = null;

	public static void register(CallUiAdapter instance) {
		CallUi.instance = instance;
	}

	public static void openCall(Contact contact) {
		instance.openCall(contact);
	}

	public static void openChat(Contact contact) {
		instance.openChat(contact);
	}

	public static void updateCallStats(Contact contact, float incomingSpeed, long incomingTotal, float outgoingSpeed,
			long outgoingTotal) {
		instance.updateCallStats(contact, incomingSpeed, incomingTotal, outgoingSpeed, outgoingTotal);
	}

	public static interface CallUiAdapter {

		public abstract void openCall(Contact contact);

		public abstract void openChat(Contact contact);

		public abstract void updateCallStats(Contact contact, float incomingSpeed, long incomingTotal, float outgoingSpeed,
				long outgoingTotal);

	}

}
