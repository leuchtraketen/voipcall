package call.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

import call.AbstractConnection;
import call.CallFactory;
import call.Connection;
import call.Client;
import call.Contact;
import call.SocketUtil;
import call.Util;

public class CallAction extends AbstractConnection {

	private final JHoverButton callbutton;

	public CallAction(Contact contact, JHoverButton callbutton) {
		super(contact);
		this.callbutton = callbutton;
	}

	public void buttonclick() {
		callbutton.setIcon(Resources.ICON_CONNECT_CALL);
		callbutton.setEnabled(false);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!isConnected()) {
					start();
				} else {
					close();
				}

				updatebutton();
			}
		}).start();
	}

	void updatebutton() {
		if (!isConnected()) {
			callbutton.setIcon(Resources.ICON_START_CALL, Resources.ICON_START_CALL_HOVER);
		} else {
			callbutton.setIcon(Resources.ICON_STOP_CALL, Resources.ICON_STOP_CALL_HOVER);
		}
		callbutton.setEnabled(true);
	}

	private void start() {
		Util.msg(contact).println("Call...", Color.green);
		try {
			Client client = Client.connect(contact.getHost(), contact.getPort(), SocketUtil.RequestType.Call);
			Thread thr = new Thread(client);
			thr.start();
			//Util.msg(contact).println("Connected.", Color.green);
		} catch (Exception e) {
			Util.msg(contact).println("Call failed :(", Color.red);
			Util.msg(contact).println("Error: " + e.getLocalizedMessage(), Color.red);
			e.printStackTrace();
			close();
		}
	}

	/*
	 * @Override public boolean isConnected() { return getConnection() != null;
	 * }
	 */

	@Override
	public void close() {
		Util.msg(contact).println("Disconnected.", Color.green);
		updatebutton();
		super.close();
	}

	@Override
	public void open() {
		Util.msg(contact).println("Connected.", Color.green);
		updatebutton();
		super.open();
	}

	@Override
	public String getId() {
		return "CallAction<" + contact + ">";
	}

	public Action getActionListener() {
		return new Listener();
	}

	private class Listener extends AbstractAction implements ActionListener {
		private static final long serialVersionUID = -2894054980727988921L;

		@Override
		public synchronized void actionPerformed(ActionEvent event) {
			buttonclick();
		}
	}
}