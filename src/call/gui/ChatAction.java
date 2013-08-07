package call.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import call.AbstractId;
import call.ChatCapture;
import call.ChatClient;
import call.Contact;
import call.Util;

public class ChatAction extends AbstractId {

	private final Contact contact;
	private final JTextField chatfield;
	private final JHoverButton chatbutton;

	public ChatAction(Contact contact, JTextField chatfield, JHoverButton chatbutton) {
		this.contact = contact;
		this.chatfield = chatfield;
		this.chatbutton = chatbutton;
		updateChatForm(false);
	}

	private synchronized void sendmessage() {
		final String msg = chatfield.getText();
		if (msg.length() > 0) {
			try {
				chatfield.setText("");
				updateChatForm(true);
				sendmessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Util.sleep(1000);
				chatfield.setText(msg);
				chatfield.setCaretPosition(msg.length());
				Util.msg(contact).println("Error: " + e.getLocalizedMessage(), Color.red);
			}
		}
		updateChatForm(false);
	}

	void updateChatForm(boolean sending) {
		if (sending) {
			chatfield.setEditable(false);
			chatbutton.setIcon(Resources.ICON_CONNECT_CHAT, Resources.ICON_CONNECT_CHAT);
			chatbutton.setEnabled(true);
			chatbutton.repaint();
		} else {
			chatfield.setEditable(true);
			if (contact.isReachable())
				chatbutton.setIcon(Resources.ICON_START_CHAT, Resources.ICON_START_CHAT_HOVER);
			else
				chatbutton.setIcon(Resources.ICON_START_CHAT_DISABLED, Resources.ICON_START_CHAT_DISABLED);
			chatbutton.setEnabled(true);
			chatbutton.repaint();
		}
	}

	private void sendmessage(String msg) throws IOException {
		Util.log(this, "sendmessage: " + msg + " (start)");
		ChatClient client = new ChatClient(contact);
		client.saveTo(new ChatCapture(contact));
		client.sendMessage(msg);
		client.close();
		Util.log(this, "sendmessage: " + msg + " (done)");
	}

	@Override
	public String getId() {
		return "ChatAction<" + contact + ">";
	}

	private class Handler implements Runnable {
		@Override
		public void run() {
			sendmessage();
		}
	}

	public Action getActionListener() {
		return new ChatActionListener();
	}

	private class ChatActionListener extends AbstractAction implements ActionListener {
		private static final long serialVersionUID = -2894054980727988921L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new Thread(new Handler()).start();
		}
	}

	public KeyListener getKeyListener() {
		return new ChatKeyListener();
	}

	private class ChatKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				new Thread(new Handler()).start();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}

	}

}