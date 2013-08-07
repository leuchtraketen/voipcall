package call.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import call.AbstractCallConnection;
import call.AbstractId;
import call.CallClient;
import call.CallFactory;
import call.Contact;
import call.Util;

public class CallAction extends AbstractId {

	private final JHoverButton callbutton;
	private final Contact contact;

	public CallAction(Contact contact, JHoverButton callbutton) {
		this.contact = contact;
		this.callbutton = callbutton;
		updatebutton();
	}

	public Action getActionListener() {
		return new Listener();
	}

	public void buttonclick() {
		callbutton.setIcon(Resources.ICON_CONNECT_CALL);
		callbutton.setEnabled(false);

		new Thread(new Runnable() {
			@Override
			public void run() {
				switch (CallFactory.getCallState(contact)) {
				case CLOSED:
					start();
					break;
				case CONNECTING:
					stop();
					break;
				case OPEN:
					stop();
					break;
				default:
					break;

				}

				updatebutton();
			}
		}).start();
	}

	void updatebutton() {
		switch (CallFactory.getCallState(contact)) {
		case CLOSED:
			if (contact.isReachable())
				callbutton.setIcon(Resources.ICON_START_CALL, Resources.ICON_START_CALL_HOVER);
			else
				callbutton.setIcon(Resources.ICON_START_CALL_DISABLED, Resources.ICON_START_CALL_DISABLED);
			callbutton.setEnabled(true);
			break;
		case CONNECTING:
			callbutton.setIcon(Resources.ICON_CONNECT_CALL);
			callbutton.setEnabled(false);
			break;
		case OPEN:
			callbutton.setIcon(Resources.ICON_STOP_CALL, Resources.ICON_STOP_CALL_HOVER);
			callbutton.setEnabled(true);
			break;
		default:
			break;

		}
	}

	private void start() {
		Util.msg(contact).println("Call...", Color.green);
		try {
			CallClient client = new CallClient(contact);
			@SuppressWarnings("unused")
			Thread thread = client.startCall();
			openCall();
			// Util.msg(contact).println("Connected.", Color.green);
		} catch (Exception e) {
			Util.msg(contact).println("Call failed :(", Color.red);
			Util.msg(contact).println("Error: " + e.getLocalizedMessage(), Color.red);
			e.printStackTrace();
			CallFactory.closeCall(contact);
		}
	}

	private void stop() {
		CallFactory.closeCall(contact);
	}

	@Override
	public String getId() {
		return "CallAction<" + contact + ">";
	}

	public void openCall() {
		new CallActionConnection(contact);
		updatebutton();
	}

	private class CallActionConnection extends AbstractCallConnection {

		public CallActionConnection(Contact contact) {
			super(contact);
		}

		@Override
		public void onCallClose() {
			Util.msg(contact).println("Disconnected.", Color.green);
			updatebutton();
			super.onCallClose();
		}

		@Override
		public void onCallOpen() {
			Util.msg(contact).println("Connected.", Color.green);
			updatebutton();
			super.onCallOpen();
		}

		@Override
		public String getId() {
			return "CallActionConnection<" + contact + ">";
		}
	}

	private class Listener extends AbstractAction implements ActionListener {
		private static final long serialVersionUID = -2894054980727988921L;

		@Override
		public synchronized void actionPerformed(ActionEvent event) {

			buttonclick();
		}
	}
}