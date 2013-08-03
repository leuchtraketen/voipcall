package call.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.Action;

import call.Client;
import call.Contact;
import call.ContactList;
import call.Id;
import call.SocketUtil;
import call.Util;

public class ChatAction implements Id {

	private final Contact contact;

	public ChatAction(Contact contact) {
		this.contact = contact;
	}

	private void sendmessage() {
		final String msg = "";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Client client = Client.connect(contact.getHost(), contact.getPort(),
							SocketUtil.RequestType.Chat);
					Socket socket = client.getSocket();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					pw.println(msg);
					pw.close();
					socket.close();
					Util.msg(ContactList.me()).println(ContactList.me(), Color.blue, msg);
				} catch (Exception e) {}
			}
		}).start();
	}

	@Override
	public String getId() {
		return "ChatAction<" + contact + ">";
	}

	public Action getActionListener() {
		return new Listener();
	}

	private class Listener extends AbstractAction implements ActionListener {
		private static final long serialVersionUID = -2894054980727988921L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sendmessage();
		}
	}

}