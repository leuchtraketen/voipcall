package call;

public abstract class AbstractCallConnection extends AbstractId implements CallConnection {

	protected Contact contact;

	public AbstractCallConnection(Contact contact) {
		setContact(contact);
	}

	protected void setContact(Contact contact) {
		this.contact = contact;
		if (contact != null) {
			final Call call = getCall();
			if (call != null) {
				call.addConnection(this);
			} else {
				throw new RuntimeException("This should never happen (AbstractConnection).");
			}
		}
	}

	private Call getCall() {
		return CallFactory.getCall(contact);
	}

	@Override
	public boolean isCallOpen() {
		Call call = getCall();
		if (call != null)
			return call.getState().equals(ConnectionState.OPEN);
		else
			return false;
	}

	@Override
	public void onCallClose() {
		System.out.println("onClose(): " + this.getId());
	}

	@Override
	public void onCallOpen() {
		System.out.println("onOpen(): " + this.getId());
	}

	public Contact getContact() {
		return contact;
	}
}