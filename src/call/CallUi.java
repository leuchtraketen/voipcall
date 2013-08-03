package call;

public class CallUi {
	private static CallUiAdapter instance = null;

	public static void register(CallUiAdapter instance) {
		CallUi.instance = instance;
	}

	public static void openCall(Contact contact) {
		instance.openCall(contact);
	}

	public static void updateCall(Contact contact) {
		instance.updateCall(contact);
	}

	public static void addUiListener(Contact contact, Connection connection) {
		UiListener listener = new UiListener(contact);
		connection.addOpenListener(listener);
		connection.addCloseListener(listener);
		Util.log("fuck", "addUiListener");

	}

	public static interface CallUiAdapter {

		public abstract void updateCall(Contact contact);

		public abstract void openCall(Contact contact);

	}

	public static class UiListener extends AbstractConnection {
		private Contact contact;

		public UiListener(Contact contact) {
			this.contact = contact;
		}

		@Override
		public String getId() {
			return "UiListener<" + contact + ">";
		}

		@Override
		public void open() {
			updateCall(contact);
			Util.log("fuck", "UiListener.open()");

			super.open();
		}

		@Override
		public void close() {
			updateCall(contact);
			super.close();
		}
	}

}
