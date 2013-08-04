package call.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import call.Contact;
import call.ContactList;

public class Resources {

	// resources
	public static final String TEXT_START_CALL = "Call";
	public static final String TEXT_STOP_CALL = "End Call";
	public static final String TEXT_CONNECT_CALL = "Connect...";
	public static final String TEXT_START_CHAT = "Send";
	public static final String TEXT_CONNECT_CHAT = "Sending";

	public static final ImageIcon ICON_START_CALL = new ImageIcon("img/start-call.png", TEXT_START_CALL);
	public static final ImageIcon ICON_START_CALL_HOVER = new ImageIcon("img/start-call-hover.png",
			TEXT_START_CALL);
	public static final ImageIcon ICON_STOP_CALL = new ImageIcon("img/stop-call.png", TEXT_STOP_CALL);
	public static final ImageIcon ICON_STOP_CALL_HOVER = new ImageIcon("img/stop-call-hover.png",
			TEXT_STOP_CALL);
	public static final ImageIcon ICON_CONNECT_CALL = new ImageIcon("img/connect-call.png", TEXT_CONNECT_CALL);
	public static final ImageIcon ICON_START_CHAT = new ImageIcon("img/start-chat.png", TEXT_START_CHAT);
	public static final ImageIcon ICON_START_CHAT_HOVER = new ImageIcon("img/start-chat-hover.png",
			TEXT_START_CHAT);
	public static final ImageIcon ICON_CONNECT_CHAT = new ImageIcon("img/connect-chat.png", TEXT_CONNECT_CHAT);

	public static final String TEXT_USER_ONLINE = "Online";
	public static final String TEXT_USER_OFFLINE = "Offline";
	public static final String TEXT_USER_UNREACHABLE = "Unreachable";

	public static final ImageIcon ICON_USER_ONLINE = new ImageIcon("img/user-available.png", TEXT_USER_ONLINE);
	public static final ImageIcon ICON_USER_OFFLINE = new ImageIcon("img/user-offline.png", TEXT_USER_OFFLINE);
	public static final ImageIcon ICON_USER_UNREACHABLE = new ImageIcon("img/user-unreachable.png",
			TEXT_USER_OFFLINE);

	public static final String TEXT_TAB_CLOSE = "Close Tab";

	public static final ImageIcon ICON_TAB_CLOSE = new ImageIcon("img/tab-close.png", TEXT_TAB_CLOSE);
	public static final ImageIcon ICON_TAB_CLOSE_HOVER = new ImageIcon("img/tab-close-hover.png",
			TEXT_TAB_CLOSE);

	public static final Font TEXT_FONT = new Font("Sans", Font.PLAIN, 12);

	public static final Color COLOR_CHAT_ME = new Color(0x09, 0x8d, 0xde);
	public static final Color COLOR_CHAT_PEER = new Color(0x00, 0x63, 0xd8);

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
