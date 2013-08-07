package call.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import call.AbstractId;
import call.CallUi;

public class MainWindow extends AbstractId {

	private static final String WINDOW_TITLE = "Call";

	// swing components
	private final JFrame window;
	private final JSplitPane horizontalSplitPane;

	// GUI classes
	private final MainTabs tabs;
	private final MainMenu menu;
	private final ConsoleTab console;
	private final ContactListGui contacts;

	public MainWindow() {
		CallUi.register(new GuiAdapter(this));

		tabs = new MainTabs(this);
		contacts = new ContactListGui(this);

		window = new JFrame();
		window.setTitle(WINDOW_TITLE);
		menu = new MainMenu(this);
		window.setJMenuBar(menu.getJMenuBar());

		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitPane.setOneTouchExpandable(true);
		JComponent contactsPanel = contacts.getComponent();
		horizontalSplitPane.setDividerLocation(contactsPanel.getPreferredSize().width);
		horizontalSplitPane.add(contactsPanel);
		horizontalSplitPane.add(tabs.getComponent());
		window.add(BorderLayout.CENTER, horizontalSplitPane);

		console = new ConsoleTab(this);
	}

	public void runGui() {
		tabs.tabs.setPreferredSize(new Dimension(750, 350));
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);
		window.setIconImage(new ImageIcon("img/icon.png").getImage());
		window.setVisible(true);
	}

	public MainTabs getTabs() {
		return tabs;
	}

	public MainMenu getMenu() {
		return menu;
	}

	public ConsoleTab getConsole() {
		return console;
	}

	public ContactListGui getContacts() {
		return contacts;
	}

	@Override
	public String getId() {
		return "MainGui";
	}

}
