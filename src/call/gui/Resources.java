package call.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import call.Contact;
import call.ContactList;

public class Resources {

	// tab names
	public static final String TABNAME_CONSOLE = "Terminal";
	public static final String TABNAME_SETTINGS_AUDIO = "Audio Devices";

	// button texts
	public static final String TEXT_START_CALL = "Call";
	public static final String TEXT_STOP_CALL = "End Call";
	public static final String TEXT_CONNECT_CALL = "Connect...";
	public static final String TEXT_START_CHAT = "Send";
	public static final String TEXT_CONNECT_CHAT = "Sending";

	// icons
	public static final ImageIcon ICON_START_CALL = new ImageIcon("img/start-call.png", TEXT_START_CALL);
	public static final ImageIcon ICON_START_CALL_HOVER = new ImageIcon("img/start-call-hover.png",
			TEXT_START_CALL);
	public static final ImageIcon ICON_START_CALL_DISABLED = new ImageIcon("img/start-call-disabled.png",
			TEXT_START_CALL);
	public static final ImageIcon ICON_STOP_CALL = new ImageIcon("img/stop-call.png", TEXT_STOP_CALL);
	public static final ImageIcon ICON_STOP_CALL_HOVER = new ImageIcon("img/stop-call-hover.png",
			TEXT_STOP_CALL);
	public static final ImageIcon ICON_CONNECT_CALL = new ImageIcon("img/connect-call.png", TEXT_CONNECT_CALL);
	public static final ImageIcon ICON_START_CHAT = new ImageIcon("img/start-chat.png", TEXT_START_CHAT);
	public static final ImageIcon ICON_START_CHAT_HOVER = new ImageIcon("img/start-chat-hover.png",
			TEXT_START_CHAT);
	public static final ImageIcon ICON_START_CHAT_DISABLED = new ImageIcon("img/start-chat-disabled.png",
			TEXT_STOP_CALL);
	public static final ImageIcon ICON_CONNECT_CHAT = new ImageIcon("img/connect-chat.png", TEXT_CONNECT_CHAT);

	// tooltip texts
	public static final String TEXT_USER_ONLINE = "Online";
	public static final String TEXT_USER_OFFLINE = "Offline";
	public static final String TEXT_USER_UNREACHABLE = "Unreachable";

	// info labels in chat tab
	public static final String LABEL_PING = "Ping:";
	public static final String LABEL_UPTIME = "Online:";
	public static final String LABEL_INCOMING = "Traffic (in):";
	public static final String LABEL_OUTGOING = "Traffic (out):";

	// default info field texts in chat tab
	public static final String TEXT_PING_NOT_SUPPORTED = "not supported";
	public static final String TEXT_PING_OFFLINE = "offline";
	public static final String TEXT_PING_UNKNOWN = "unknown";
	public static final String TEXT_CALLSTATS_INCOMING = "unknown";
	public static final String TEXT_CALLSTATS_OUTGOING = "unknown";

	// icons in contact list
	public static final ImageIcon ICON_USER_ONLINE = new ImageIcon("img/user-available.png", TEXT_USER_ONLINE);
	public static final ImageIcon ICON_USER_OFFLINE = new ImageIcon("img/user-offline.png", TEXT_USER_OFFLINE);
	public static final ImageIcon ICON_USER_UNREACHABLE = new ImageIcon("img/user-unreachable.png",
			TEXT_USER_OFFLINE);

	// labels in audio settings
	public static final String LABEL_SETTINGS_AUDIO_DEFAULT_MICROPHONE = "Default Microphone:";
	public static final String LABEL_SETTINGS_AUDIO_DEFAULT_SPEAKER = "Default Speaker:";
	public static final String LABEL_SETTINGS_AUDIO_CALL_BUFFER = "<html>Buffer size:<br>&nbsp;</html>";
	public static final String LABEL_SETTINGS_AUDIO_SAMPLING_RATE = "<html>Sampling rate:<br>&nbsp;</html>";
	public static final String LABEL_SETTINGS_AUDIO_SAMPLE_SIZE = "Sample size:";
	public static final String LABEL_SETTINGS_AUDIO_SELECTED_ENCODING = "<html>Selected Encoding:<br>&nbsp;</html>";

	// closeable tabs
	public static final String TEXT_TAB_CLOSE = "Close Tab";
	public static final ImageIcon ICON_TAB_CLOSE = new ImageIcon("img/tab-close.png", TEXT_TAB_CLOSE);
	public static final ImageIcon ICON_TAB_CLOSE_HOVER = new ImageIcon("img/tab-close-hover.png",
			TEXT_TAB_CLOSE);

	// default fonts
	public static final Font FONT_TEXT = new Font("Sans", Font.PLAIN, 12);
	public static final Font FONT_LIST = new Font("Sans", Font.PLAIN, 12);
	public static final Font FONT_TABTITLE = new Font("Sans", Font.PLAIN, 12);
	public static final Font FONT_CONSOLE = new Font("Monospaced", Font.PLAIN, 12);

	// default chat colors
	public static final Color COLOR_CHAT_ME = new Color(0x09, 0x8d, 0xde);
	public static final Color COLOR_CHAT_PEER = new Color(0x00, 0x63, 0xd8);

	// menu icons
	public static final Icon ICON_CONTACTS_RELOAD = new ImageIcon("img/contacts-reload.png");
	public static final Icon ICON_CONTACTS_ADD = new ImageIcon("img/contacts-add.png");
	public static final Icon ICON_CONSOLE = new ImageIcon("img/console.png");
	public static final Icon ICON_SETTINGS_AUDIO = new ImageIcon("img/microphone.png");
	public static final Icon ICON_HELP_ABOUT = new ImageIcon("img/help-about.png");

	public static String getToolTipText(Contact value) {
		if (value.isUnreachable()) {
			return Resources.TEXT_USER_UNREACHABLE;
		} else if (ContactList.isOnline(value)) {
			return Resources.TEXT_USER_ONLINE;
		} else {
			return Resources.TEXT_USER_OFFLINE;
		}
	}

	public static Icon getIcon(Contact value) {
		if (value.isUnreachable()) {
			return Resources.ICON_USER_UNREACHABLE;
		} else if (ContactList.isOnline(value)) {
			return Resources.ICON_USER_ONLINE;
		} else {
			return Resources.ICON_USER_OFFLINE;
		}
	}
}
