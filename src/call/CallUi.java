package call;

public class CallUi {

	private static AbstractCallUi instance = null;

	public static abstract class AbstractCallUi {

		public AbstractCallUi() {
			instance = this;
		}

		protected abstract void openCall(Contact contact);

		protected abstract void setConnection(Contact contact, Peer peer);

	}

	public static void openCall(Contact contact) {
		instance.openCall(contact);
	}

	public static void setConnection(Contact contact, Peer peer) {
		instance.setConnection(contact, peer);
	}
}
