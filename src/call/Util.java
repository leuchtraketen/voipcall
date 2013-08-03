package call;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.UIManager;

public class Util {

	private static SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static String OS = System.getProperty("os.name").toLowerCase();
	public static LogProvider currentLogProvider = null;
	private static final Map<Contact, MessageOutput> messageOutputs = new HashMap<Contact, MessageOutput>();

	public static void setLogProvider(LogProvider log) {
		Util.currentLogProvider = log;
	}

	public static void setMessageOutput(Contact contact, MessageOutput msg) {
		messageOutputs.put(contact, msg);
	}

	public static void log(String id, String msg) {
		System.out.println("[" + id + "] " + msg);
	}

	public static void log(Id id, String msg) {
		System.out.println("[" + id.getId() + "] " + msg);
	}

	public static Msg msg(Contact contact) {
		MessageOutput msgout = messageOutputs.containsKey(contact) ? messageOutputs.get(contact) : null;
		return new Msg(msgout);
	}

	public static class Msg {
		private final MessageOutput msgout;

		private Msg(MessageOutput msgout) {
			this.msgout = msgout;
		}

		public void start() {
			print(formatDateTime(System.currentTimeMillis()), Color.gray);
			printspace();
		}

		public void stop() {
			print("\n");
		}

		public void println(String str, Color color, String str2) {
			start();
			print(str, color);
			printspace();
			print(str2);
			stop();
		}

		private void printspace() {
			print(" ");
		}

		public void println(Contact contact, Color color, String str2) {
			start();
			print(contact, color);
			printspace();
			print(str2);
			stop();
		}

		private void print(Contact contact, Color color) {
			print(Util.firstToUpperCase(contact.getUser()), color);
		}

		public void println(String str, Color color) {
			start();
			print(str, color);
			stop();
		}

		public void println(String str) {
			start();
			print(str);
			stop();
		}

		public void print(String str) {
			print(str, Color.black);
		}

		public void print(String str, Color color) {
			if (msgout != null) {
				msgout.append(str, color);
			} else {
				System.out.println("msg: \"" + str + "\"");
			}
		}
	}

	public static boolean sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public static String firstToUpperCase(String str) {
		if (str.length() > 0) {
			if (str.length() > 1) {
				return Character.toUpperCase(str.charAt(0)) + str.substring(1);
			} else {
				return str.toUpperCase();
			}
		} else {
			return str;
		}
	}

	public static String formatDateTime(long millis) {
		Date resultdate = new Date(millis);
		return datetime.format(resultdate);
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isUnix() {
		return !isWindows();
	}

	public static String getUserName() {
		return System.getProperty("user.name").split(" ")[0];
	}

	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			return;
		} catch (Exception e) {}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
	}

	public static <T> Set<T> asSet(T[] array) {
		return new HashSet<T>(Arrays.asList(array));
	}

}
