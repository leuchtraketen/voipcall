package call.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import call.Activatable;
import call.Client;
import call.CloseListener;
import call.CloseListener.CloseListening;
import call.Contact;
import call.Contacts;
import call.LogProvider;
import call.MessageOutput;
import call.SocketUtil;
import call.Util;

public class ChatGui {

	private final JTextPane area;
	private final ChatPanel panel;
	private final Contact contact;
	private final JButton callbutton;
	private final JButton chatbutton;
	private final CallAction callaction;
	private final ChatAction chataction;
	private Activatable connection;

	private static final Map<Contact, ChatGui> instances = new HashMap<Contact, ChatGui>();
	private static final String TEXT_START_CALL = "Call";
	private static final String TEXT_STOP_CALL = "End Call";
	private static final String TEXT_CONNECT_CALL = "Connect...";
	private static ImageIcon ICON_START_CALL;
	private static ImageIcon ICON_STOP_CALL;
	private static ImageIcon ICON_CONNECT_CALL;

	public static ChatGui getInstance(Contact c) {
		if (instances.containsKey(c)) {
			return instances.get(c);
		} else {
			ChatGui instance = new ChatGui(c);
			instances.put(c, instance);
			return instance;
		}
	}

	private ChatGui(Contact contact) {
		this.contact = contact;

		panel = new ChatPanel(this);
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new BorderLayout());
		area = new JTextPane();

		// area
		new JTextPaneMessageOutput(contact, area);
		area.setPreferredSize(new Dimension(500, 350));
		Font font = new Font("Sans", Font.PLAIN, 12);
		area.setFont(font);
		area.setEditable(false);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// scrollpane
		JScrollPane areaPane = new JScrollPane(area);
		panel.add(BorderLayout.CENTER, areaPane);

		ICON_START_CALL = new ImageIcon("img/start-call.png", TEXT_START_CALL);
		ICON_STOP_CALL = new ImageIcon("img/stop-call.png", TEXT_STOP_CALL);
		ICON_CONNECT_CALL = new ImageIcon("img/connect-call.png", TEXT_CONNECT_CALL);

		// call panel
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(BorderLayout.NORTH, buttonpanel);

		// call button
		callbutton = new JButton(ICON_START_CALL);
		callaction = new CallAction();
		callbutton.addActionListener(callaction);
		buttonpanel.add(callbutton);
		callbutton.setBorderPainted(false);
		callbutton.setFocusPainted(false);
		callbutton.setContentAreaFilled(false);

		// chat panel
		JPanel chatpanel = new JPanel();
		chatpanel.setLayout(new BorderLayout());
		chatpanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(BorderLayout.SOUTH, chatpanel);

		// chat button
		chatbutton = new JButton("Send");
		chataction = new ChatAction();
		chatbutton.addActionListener(chataction);
		chatpanel.add(chatbutton);
	}

	private class ChatAction implements ActionListener, CloseListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sendmessage();
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
						Util.msg(Contacts.me()).println(Contacts.me(), Color.blue, msg);
					} catch (Exception e) {}
				}
			}).start();
		}

		@Override
		public boolean isActive() {
			return false;
		}

		@Override
		public void close() {}

		@Override
		public void onClose() {}
	}

	private class CallAction implements ActionListener, CloseListener {

		@Override
		public synchronized void actionPerformed(ActionEvent event) {
			callbutton.setIcon(ICON_CONNECT_CALL);
			area.repaint();

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (connection == null) {
						start();
					} else {
						stop();
					}

					updatebutton();
				}
			}).start();
		}

		private void updatebutton() {
			if (connection == null) {
				callbutton.setIcon(ICON_START_CALL);
				area.repaint();
			} else {
				callbutton.setIcon(ICON_STOP_CALL);
				area.repaint();
			}
		}

		private void start() {
			Util.msg(contact).println("Call...", Color.green);
			try {
				Client client = Client.connect(contact.getHost(), contact.getPort(),
						SocketUtil.RequestType.Call);
				client.closeListeners.add(this);
				Thread thr = new Thread(client);
				ChatGui.this.connection = client;
				thr.start();
				Util.msg(contact).println("Connected.", Color.green);
			} catch (Exception e) {
				Util.msg(contact).println("Call failed :(", Color.red);
				Util.msg(contact).println("Error: " + e.getLocalizedMessage(), Color.red);
				e.printStackTrace();
				if (connection != null)
					connection.close();
				connection = null;
			}
		}

		private void stop() {
			if (connection != null)
				connection.close();
			connection = null;
			Util.msg(contact).println("Disconnected.", Color.green);
		}

		@Override
		public void onClose() {
			stop();
			updatebutton();
		}

		@Override
		public boolean isActive() {
			return connection != null;
		}

		@Override
		public void close() {
			onClose();
		}
	}

	public JComponent getComponent() {
		return panel;
	}

	public static class JTextPaneMessageOutput implements LogProvider, MessageOutput {
		@SuppressWarnings("unused")
		private final Contact contact;
		private final JTextPane area;

		public JTextPaneMessageOutput(Contact contact, JTextPane area) {
			super();
			this.contact = contact;
			this.area = area;
			Util.setMessageOutput(contact, this);
		}

		@Override
		public void append(String str, Color c) {

			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Sans");
			aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

			/*
			 * int len = area.getDocument().getLength();
			 * area.setCaretPosition(len); area.setCharacterAttributes(aset,
			 * false); area.replaceSelection(str);
			 */

			DefaultStyledDocument document = (DefaultStyledDocument) area.getDocument();
			try {
				document.insertString(document.getEndPosition().getOffset(), str, aset);
			} catch (BadLocationException e) {}

		}

		@Override
		public String getLog() {
			return area.getText();
		}
	}

	public Activatable getConnection() {
		return connection;
	}

	public void setConnection(CloseListening connection) {
		this.connection = connection;
		connection.getCloseListeners().add(callaction);
		callaction.updatebutton();
	}

	public Contact getContact() {
		return contact;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return hashCode() == obj.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return contact.hashCode() + 17;
	}

	public class ChatPanel extends JPanel {
		private static final long serialVersionUID = -2089483699485903634L;
		private ChatGui chatgui;

		public ChatPanel(ChatGui chatgui) {
			this.chatgui = chatgui;
		}

		public ChatGui getChatGui() {
			return chatgui;
		}
	}
}
