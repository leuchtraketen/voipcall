package call;

public abstract class AbstractConnection extends AbstractId implements Connection {

	protected Contact contact;
	private boolean inOpen = false;
	private boolean inClose = false;

	public AbstractConnection(Contact contact) {
		this.contact = contact;
	}

	protected void setContact(Contact contact) {
		this.contact = contact;
	}

	private Call getCall() {
		return CallFactory.getCall(contact);
	}

	@Override
	public boolean isConnected() {
		return getCall() != null;
	}

	@Override
	public void close() {
		if (!inClose) {
			inClose = true;
			System.out.println("close(): " + this.getId());
			//if (isConnected()) {
				CallFactory.closeCall(contact);
			//}
			inClose = false;
		}
	}

	@Override
	public void open() {
		if (!inOpen) {
			inOpen = true;
			System.out.println("open(): " + this.getId());
			CallFactory.openCall(contact);
			inOpen = false;
		}
	}

	public Contact getContact() {
		return contact;
	}
}