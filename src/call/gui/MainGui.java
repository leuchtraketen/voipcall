package call.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import call.Activatable;
import call.Util;
import call.gui.ChatGui.ChatPanel;

public class MainGui {

	private static final String WINDOW_TITLE = "Call";

	private final JFrame window;
	private final JTabbedPane tabs;
	private final JSplitPane horizontalSplitPane;

	private static MainGui instance;
	private final ContactsGui contactsGui;

	public static MainGui getInstance() {
		if (instance == null) {
			instance = new MainGui();
		}
		return instance;
	}

	private MainGui() {
		window = new JFrame();
		window.setTitle(WINDOW_TITLE);
		tabs = new JTabbedPane();

		contactsGui = new ContactsGui();
		JComponent contactsPanel = contactsGui.getComponent();

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitPane.setOneTouchExpandable(true);
		horizontalSplitPane.setDividerLocation(contactsPanel.getPreferredSize().width);
		horizontalSplitPane.add(contactsPanel);
		horizontalSplitPane.add(tabs);
		window.add(BorderLayout.CENTER, horizontalSplitPane);

		addTab("Terminal Output", new ConsoleGui().getComponent());
	}

	public void addTab(String name, JComponent component) {
		Util.log("tabs", "try add: " + name);
		if (tabs.indexOfTab(name) == -1) {
			Util.log("tabs", "add: " + name);
			tabs.addTab(name, component);
		}
	}

	public void showTab(String name) {
		Util.log("tabs", "try show: " + name);
		int index = tabs.indexOfTab(name);
		if (index != -1) {
			tabs.setSelectedIndex(index);
			Util.log("tabs", "show: " + name);
		}
	}

	public void closeInactiveTabs() {
		for (int i = tabs.getTabCount() - 1; i >= 0; --i) {
			Object component = tabs.getComponentAt(i);
			if (component instanceof ChatPanel) {
				ChatGui chatgui = ((ChatPanel) component).getChatGui();
				Activatable connection = chatgui.getConnection();
				if (connection == null || !connection.isActive()) {
					tabs.remove((Component) component);
				}
			}
		}
	}

	public void runGui() {
		window.setSize(600, 350);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);
		window.setIconImage(new ImageIcon("img/icon.png").getImage());
		window.setVisible(true);
	}
}
