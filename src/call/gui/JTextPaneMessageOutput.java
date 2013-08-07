package call.gui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import call.Contact;
import call.LogProvider;
import call.MessageOutput;
import call.Util;

public class JTextPaneMessageOutput implements LogProvider, MessageOutput {
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
		 * int len = area.getDocument().getLength(); area.setCaretPosition(len);
		 * area.setCharacterAttributes(aset, false); area.replaceSelection(str);
		 */

		DefaultStyledDocument document = (DefaultStyledDocument) area.getDocument();
		try {
			if (area.getText().length() < 3)
				area.setText("");
			// document.insertString(0, str, aset);
			// else
			document.insertString(document.getEndPosition().getOffset() - 1, str, aset);
		} catch (BadLocationException e) {}

	}

	@Override
	public String getLog() {
		return area.getText();
	}

	@Override
	public void close() {}
}