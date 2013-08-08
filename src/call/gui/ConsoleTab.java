package call.gui;

import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import call.AbstractId;
import call.Config;
import call.Config.Option;
import call.ConfigListener;
import call.Id;
import call.Util;

public class ConsoleTab extends AbstractId implements ConfigListener {

	private final MainWindow main;
	private final JTextArea area;
	private final JScrollPane areaPane;

	public ConsoleTab(MainWindow main) {
		this.main = main;

		// area
		area = new JTextArea();
		Font font = Resources.FONT_CONSOLE;
		area.setFont(font);
		area.setEditable(false);
		DefaultCaret caret = (DefaultCaret) area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		areaPane = new JScrollPane(area);

		// system output
		PrintStream windowStream = new PrintStream(new JTextAreaOutputStream(area));
		Util.setOutAndErr(windowStream);

		// config listener
		Config.addConfigListener(this);
		Config.notifyConfigListener(this);
	}

	public JComponent getComponent() {
		return areaPane;
	}

	private void showConsoleTab() {
		main.getTabs().removeTab(Resources.TABNAME_CONSOLE);
		main.getTabs().addTab(Resources.TABNAME_CONSOLE, areaPane, Resources.ICON_CONSOLE);
	}

	private void hideConsoleTab() {
		main.getTabs().removeTab(Resources.TABNAME_CONSOLE);
	}

	@Override
	public void onConfigUpdate(Option option, float value) {}

	@Override
	public void onConfigUpdate(Option option, int value) {}

	@Override
	public void onConfigUpdate(Option option, boolean value) {
		if (option.equals(Config.SHOW_CONSOLE)) {
			if (value)
				showConsoleTab();
			else
				hideConsoleTab();
		}
	}

	@Override
	public void onConfigUpdate(Option option, String value) {}

	@Override
	public void onConfigUpdate(Option option, Id value) {}

	@Override
	public String getId() {
		return "ConsoleTab";
	}

}