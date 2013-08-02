package call.gui;

import java.awt.Font;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import call.LogProvider;
import call.Util;

class ConsoleGui {

	JTextArea area = new JTextArea(20, 80);

	public ConsoleGui() {
		// area
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		area.setFont(font);
		area.setEditable(false);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// system output
		PrintStream windowStream = new PrintStream(new JTextAreaOutputStream(area));
		System.setOut(windowStream);
		System.setErr(windowStream);
	}

	public JComponent getComponent() {
		JScrollPane areaPane = new JScrollPane(area);
		return areaPane;
	}

	public static class JTextAreaOutputStream extends OutputStream implements LogProvider {
		JTextArea ta;

		public JTextAreaOutputStream(JTextArea t) {
			super();
			ta = t;
			Util.setLogProvider(this);
		}

		public void write(int i) {
			ta.append(Character.toString((char) i));
		}

		public void write(char[] buf, int off, int len) {
			String s = new String(buf, off, len);
			ta.append(s);
		}

		@Override
		public String getLog() {
			return ta.getText();
		}
	}
}