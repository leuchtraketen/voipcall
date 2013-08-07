package call;

import java.awt.Color;

public class MessageWriter {
	private final MessageOutput msgout;

	public MessageWriter(MessageOutput msgout) {
		this.msgout = msgout;
	}

	public MessageOutput getMessageOutput() {
		return msgout;
	}

	public void start() {
		print(Util.formatDateTime(System.currentTimeMillis()), Color.gray);
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