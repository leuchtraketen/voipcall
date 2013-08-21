package call.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;

import call.AbstractId;
import call.ChatCapture;
import call.Config;
import call.Contact;
import call.ContactList;
import call.ContactListUpdateListener;
import call.Ping;
import call.PingClient;
import call.Util;

public class ChatTab extends AbstractId implements PingClient.Listener, ContactListUpdateListener, Runnable {

	// instances
	private static final Map<Contact, ChatTab> instances = new HashMap<>();
	private int generation = 1;

	public static synchronized ChatTab getInstance(Contact c) {
		if (instances.containsKey(c)) {
			return instances.get(c);
		} else {
			ChatTab instance = new ChatTab(c);
			new Thread(instance, "ChatTab").start();
			instances.put(c, instance);
			return instance;
		}
	}

	// gui elements
	private final JTextPane area;
	private final ChatTabComponent panel;
	private final Contact contact;
	private final JHoverButton callbutton;
	private final JHoverButton chatbutton;
	private final CallAction callaction;
	private final ChatAction chataction;
	private final JTextField chatfield;
	private final JLabel infolabelping;
	private final JLabel infolabeluptime;
	private final JLabel infolabelincoming;
	private final JLabel infolabeloutgoing;

	// info data
	private Ping ping;

	private ChatTab(Contact contact) {
		this.contact = contact;
		// Util.log(contact, "new ChatTab");

		panel = new ChatTabComponent(this);
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(setNoGaps(new BorderLayout()));
		area = new JTextPane();

		// area
		new JTextPaneMessageOutput(contact, area);
		area.setPreferredSize(new Dimension(650, 350));
		area.setFont(Resources.FONT_TEXT);
		area.setEditable(false);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		restoreChatlog();

		// scrollpane
		JScrollPane areaPane = new JScrollPane(area);
		panel.add(BorderLayout.CENTER, areaPane);

		// call panel
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(setNoGaps(new BorderLayout()));
		buttonpanel.setBorder(BorderFactory.createEmptyBorder());
		panel.add(BorderLayout.NORTH, buttonpanel);

		// call button
		callbutton = new JHoverButton(Resources.ICON_START_CALL, Resources.ICON_START_CALL_HOVER);
		callaction = new CallAction(contact, callbutton);
		callbutton.addActionListener(callaction.getActionListener());
		buttonpanel.add(BorderLayout.WEST, callbutton);
		callbutton.setBorderPainted(false);
		callbutton.setFocusPainted(false);
		callbutton.setContentAreaFilled(false);

		// info panel
		JPanel infopanel = new JPanel();
		FlowLayout flow = new FlowLayout();
		flow.setVgap(0);
		infopanel.setLayout(flow);
		infopanel.setBorder(BorderFactory.createEmptyBorder());

		infopanel
				.add(createTwoRowsPanel(new JLabel(Resources.LABEL_PING), new JLabel(Resources.LABEL_UPTIME)));
		infolabelping = createInfoField(Resources.TEXT_PING_UNKNOWN);
		infolabeluptime = createInfoField(Resources.TEXT_PING_UNKNOWN);
		infopanel.add(createTwoRowsPanel(infolabelping, infolabeluptime));

		infopanel.add(createTwoRowsPanel(new JLabel(Resources.LABEL_INCOMING), new JLabel(
				Resources.LABEL_OUTGOING)));
		infolabelincoming = createInfoField(Resources.TEXT_CALLSTATS_INCOMING);
		infolabeloutgoing = createInfoField(Resources.TEXT_CALLSTATS_OUTGOING);
		infopanel.add(createTwoRowsPanel(infolabelincoming, infolabeloutgoing));
		updateCallStats(0, 0, 0, 0);

		buttonpanel.add(BorderLayout.EAST, infopanel);

		// chat panel
		JPanel chatpanel = new JPanel();
		chatpanel.setLayout(setNoGaps(new BorderLayout()));
		chatpanel.setBorder(BorderFactory.createEmptyBorder());

		// chat field
		chatfield = new JTextField();
		chatfield.setFont(Resources.FONT_TEXT);
		// chatfield.setPreferredSize(new Dimension(500, 50));
		// addEnterAction(chatfield, chataction.getActionListener());
		chatpanel.add(BorderLayout.CENTER, chatfield);

		// chat button
		chatbutton = new JHoverButton(Resources.ICON_START_CHAT, Resources.ICON_START_CHAT_HOVER);
		chatpanel.add(BorderLayout.EAST, chatbutton);
		chatbutton.setBorderPainted(false);
		chatbutton.setFocusPainted(false);
		chatbutton.setContentAreaFilled(false);
		chatbutton.setBorder(null);
		chatbutton.setMargin(new Insets(0, 0, 0, 0));

		// chat action
		chataction = new ChatAction(contact, chatfield, chatbutton);
		chatfield.addKeyListener(chataction.getKeyListener());
		chatbutton.addActionListener(chataction.getActionListener());

		panel.add(BorderLayout.SOUTH, chatpanel);

		// add listeners
		PingClient.addListener(contact, this);
		ContactList.addListener(this);
	}

	private JLabel createInfoField(String text) {
		JLabel info = new JLabel();
		info.setText(text);
		info.setBackground(Color.white);
		info.setOpaque(true);
		info.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createMatteBorder(0, 3, 0, 3, Color.white)));
		return info;
	}

	private Component createTwoRowsPanel(JComponent c1, JComponent c2) {
		JPanel panel = new JPanel();
		panel.setLayout(setNoGaps(new GridLayout(2, 1)));
		panel.add(c1);
		panel.add(c2);
		return panel;
	}

	private LayoutManager setNoGaps(GridLayout layout) {
		layout.setHgap(0);
		layout.setVgap(0);
		return layout;
	}

	private BorderLayout setNoGaps(BorderLayout layout) {
		layout.setHgap(0);
		layout.setVgap(0);
		return layout;
	}

	@SuppressWarnings("unused")
	private FlowLayout setNoGaps(FlowLayout layout) {
		layout.setHgap(0);
		layout.setVgap(0);
		return layout;
	}

	@SuppressWarnings("unused")
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

	public void focus() {
		chatfield.requestFocusInWindow();
	}

	@Override
	public void onPingUpdate(Contact contact) {
		ping = PingClient.getPing(contact);
	}

	@Override
	public void onContactUpdate(Contact contact) {}

	@Override
	public void onAnyContactUpdate() {}

	@Override
	public void run() {
		final int currentGeneration = generation;
		while (currentGeneration == generation) {
			if (ContactList.isOnline(contact)) {
				if (ping != null) {
					infolabelping.setText(ping.toString());
					infolabeluptime.setText(Util.formatMilliSecondsHumanReadable(System.currentTimeMillis()
							- ping.getUptime()));
				}
				Util.sleep(5_000);
			} else if (contact.isLoop()) {
				infolabelping.setText(Resources.TEXT_PING_NOT_SUPPORTED);
				infolabelping.setForeground(Color.RED);
				infolabeluptime.setText(Util.formatMilliSecondsHumanReadable(System.currentTimeMillis()
						- Config.CURRENT_UPTIME));
				Util.sleep(5_000);
			} else {
				infolabelping.setText(Resources.TEXT_PING_OFFLINE);
				infolabeluptime.setText(Resources.TEXT_PING_OFFLINE);
				Util.sleep(15_000);
			}
		}
	}

	private void restoreChatlog() {
		// deserialize chat log!
		new ChatCapture(contact).deserialize(Util.msg(contact).getMessageOutput());
	}

	public void updateCallStats(float incomingSpeed, long incomingTotal, float outgoingSpeed,
			long outgoingTotal) {
		if (incomingSpeed != -1) {
			String text = Util.formatBytesHumanReadable(incomingSpeed) + "/s";
			if (!text.equals(infolabelincoming.getText()))
				infolabelincoming.setText(text);
		}
		if (outgoingSpeed != -1) {
			String text = Util.formatBytesHumanReadable(outgoingSpeed) + "/s";
			if (!text.equals(infolabeloutgoing.getText()))
				infolabeloutgoing.setText(text);
		}
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

	public class ChatTabComponent extends JPanel {
		private static final long serialVersionUID = -2089483699485903634L;
		private ChatTab chattab;

		public ChatTabComponent(ChatTab chattab) {
			this.chattab = chattab;
		}

		public ChatTab getChatTab() {
			return chattab;
		}
	}

	@Override
	public String getId() {
		return "ChatTab";
	}
}
