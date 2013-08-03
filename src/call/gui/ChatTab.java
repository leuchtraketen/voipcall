package call.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;

import call.Contact;
import call.Util;

public class ChatTab {


	// instances
	private static final Map<Contact, ChatTab> instances = new HashMap<Contact, ChatTab>();

	public static ChatTab getInstance(Contact c) {
		if (instances.containsKey(c)) {
			return instances.get(c);
		} else {
			ChatTab instance = new ChatTab(c);
			instances.put(c, instance);
			return instance;
		}
	}

	// gui elements
	private final JTextPane area;
	private final ChatPanel panel;
	private final Contact contact;
	private final JHoverButton callbutton;
	private final JHoverButton chatbutton;
	private final CallAction callaction;
	private final ChatAction chataction;
	private final JTextArea chatfield;

	private ChatTab(Contact contact) {
		this.contact = contact;
		Util.log(contact, "new ChatTab");

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

		// call panel
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(BorderLayout.NORTH, buttonpanel);

		// call button
		callbutton = new JHoverButton(Resources.ICON_START_CALL, Resources.ICON_START_CALL_HOVER);
		callaction = new CallAction(contact, callbutton);
		callbutton.addActionListener(callaction.getActionListener());
		buttonpanel.add(callbutton);
		callbutton.setBorderPainted(false);
		callbutton.setFocusPainted(false);
		callbutton.setContentAreaFilled(false);

		// chat panel
		JPanel chatpanel = new JPanel();
		chatpanel.setLayout(new BorderLayout());
		chatpanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(BorderLayout.SOUTH, chatpanel);

		// chat field
		chatfield = new JTextArea(50, 3);
		chataction = new ChatAction(contact);
		addEnterAction(chatfield, chataction.getActionListener());
		chatpanel.add(BorderLayout.CENTER, chatfield);

		// chat button
		chatbutton = new JHoverButton(Resources.ICON_START_CHAT_SEND, Resources.ICON_START_CHAT_SEND_HOVER);
		chatbutton.addActionListener(chataction.getActionListener());
		chatpanel.add(BorderLayout.EAST, chatbutton);
		chatbutton.setBorderPainted(false);
		chatbutton.setFocusPainted(false);
		chatbutton.setContentAreaFilled(false);
	}

	private void addEnterAction(JTextArea textarea, Action action) {
		InputMap input = textarea.getInputMap();
		KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
		input.put(shiftEnter, "insert-break");
		input.put(enter, "text-submit");
		ActionMap actions = textarea.getActionMap();
		actions.put("text-submit", action);
	}

	public JComponent getComponent() {
		return panel;
	}

	public Contact getContact() {
		return contact;
	}

	public CallAction getCallaction() {
		return callaction;
	}

	public ChatAction getChataction() {
		return chataction;
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
		private ChatTab chatgui;

		public ChatPanel(ChatTab chatgui) {
			this.chatgui = chatgui;
		}

		public ChatTab getChatGui() {
			return chatgui;
		}
	}
}
