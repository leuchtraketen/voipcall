package call.gui;

import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

class ConsoleTab {

	JTextArea area = new JTextArea(20, 80);

	public ConsoleTab() {
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
}