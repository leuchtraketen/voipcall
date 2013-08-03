package call.gui;

import java.io.OutputStream;

import javax.swing.JTextArea;

import call.LogProvider;
import call.Util;

public class JTextAreaOutputStream extends OutputStream implements LogProvider {
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